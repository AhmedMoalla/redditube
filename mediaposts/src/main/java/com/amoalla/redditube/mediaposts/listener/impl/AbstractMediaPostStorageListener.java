package com.amoalla.redditube.mediaposts.listener.impl;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.mediaposts.entity.MediaPost;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.SubscribableType;
import com.amoalla.redditube.mediaposts.listener.MediaPostStorageListener;
import com.amoalla.redditube.mediaposts.repository.MediaPostRepository;
import com.amoalla.redditube.mediaposts.scraper.event.NewMediaPostsAvailableEvent;
import com.amoalla.redditube.mediaposts.storage.StorageService;
import com.amoalla.redditube.mediaposts.storage.cache.MediaHashCache;
import com.amoalla.redditube.mediaposts.storage.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractMediaPostStorageListener implements MediaPostStorageListener {

    private static final String BUCKET_NAME_FORMAT = "%s.%s";
    private static final String USER_BUCKET_PREFIX = "u";
    private static final String SUBREDDIT_BUCKET_PREFIX = "r";

    protected final MediaPostRepository repository;
    protected final StorageService storageService;
    private final ThreadPoolTaskScheduler scheduler;
    private final MediaHashCache mediaHashCache;

    protected AbstractMediaPostStorageListener(MediaPostRepository repository,
                                               StorageService storageService,
                                               ThreadPoolTaskScheduler scheduler,
                                               MediaHashCache mediaHashCache) {
        this.repository = repository;
        this.storageService = storageService;
        this.scheduler = scheduler;
        this.mediaHashCache = mediaHashCache;
    }

    @Override
    public void onNewMediaPostsAvailableEvent(NewMediaPostsAvailableEvent event) throws StorageException {
        // Do not override. Only used by CompositeMediaPostStorageListener as an @EventListener
    }

    protected void uploadAndSaveMediaPost(MediaPostDto mediaPostDto, Subscribable subscribable, String bucketName, Runnable completionCallback) {

        String mediaUrl = mediaPostDto.getMediaUrl();
        String mediaThumbnailUrl = mediaPostDto.getMediaThumbnailUrl();

        try (InputStream tempMediaData = new URL(mediaUrl).openStream()) {
            byte[] bytes = tempMediaData.readAllBytes();
            InputStream mediaData = new ByteArrayInputStream(bytes);
            InputStream thumbnailMediaData = new URL(mediaThumbnailUrl).openStream();

            String hash = DigestUtils.md5DigestAsHex(bytes);
            if (!mediaHashCache.exists(hash)) {

                MediaPost mediaPost = mapToEntity(mediaPostDto, subscribable);
                mediaPost.setHash(hash);
                mediaHashCache.add(hash);

                CompletableFuture<Void> mediaFuture = submitUploadTask(() ->
                        mediaPost.setObjectId(storageService.uploadMediaToStorage(mediaData, extractFileNameFromUrl(mediaUrl), bucketName)));
                CompletableFuture<Void> thumbnailFuture = submitUploadTask(() ->
                        mediaPost.setThumbnailObjectId(storageService.uploadMediaToStorage(thumbnailMediaData, extractFileNameFromUrl(mediaThumbnailUrl), bucketName)));
                CompletableFuture.allOf(mediaFuture, thumbnailFuture)
                        .thenAcceptAsync((Void res) -> {
                            repository.save(mediaPost);
                            completionCallback.run();
                        });
            } else {
                completionCallback.run();
            }
        } catch (Exception e) {
            log.error("Error happened while trying to upload media post {} to bucket {}", mediaPostDto, bucketName, e);
        }
    }

    private String extractFileNameFromUrl(String mediaUrl) {
        String fileName = StringUtils.getFilename(mediaUrl);
        if (fileName != null && fileName.contains("?")) {
            fileName = fileName.split("\\?")[0];
        }
        return fileName;
    }

    private CompletableFuture<Void> submitUploadTask(UploadTask uploadTask) {
        return scheduler.submitListenable(() -> {
            try {
                uploadTask.run();
            } catch (StorageException e) {
                log.error("Error happened while trying to upload media", e);
                return null;
            }
            return (Void) null;
        }).completable();
    }

    protected MediaPost mapToEntity(MediaPostDto mediaPostDto, Subscribable subscribable) {
        MediaPost mediaPost = new MediaPost();
        mediaPost.setId(mediaPostDto.getId());
        mediaPost.setTitle(mediaPostDto.getTitle());
        mediaPost.setCreationDateTime(mediaPostDto.getCreationDateTime());
        mediaPost.setOwner(subscribable);
        return mediaPost;
    }

    protected String getBucketNameForSubscribable(Subscribable subscribable) {
        String prefix = SubscribableType.USER.equals(subscribable.getType()) ? USER_BUCKET_PREFIX : SUBREDDIT_BUCKET_PREFIX;
        String handle = subscribable.getHandle();
        return String.format(BUCKET_NAME_FORMAT, prefix, handle);
    }

    interface UploadTask {
        void run() throws StorageException;
    }
}
