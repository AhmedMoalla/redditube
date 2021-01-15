package com.amoalla.redditube.mediaposts.listener.impl;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.listener.MediaPostStorageListener;
import com.amoalla.redditube.mediaposts.listener.NewSingleMediaPostAvailableEvent;
import com.amoalla.redditube.mediaposts.listener.impl.AbstractMediaPostStorageListener;
import com.amoalla.redditube.mediaposts.repository.MediaPostRepository;
import com.amoalla.redditube.mediaposts.scraper.event.NewMediaPostsAvailableEvent;
import com.amoalla.redditube.mediaposts.storage.StorageService;
import com.amoalla.redditube.mediaposts.storage.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class CompositeMediaPostStorageListener extends AbstractMediaPostStorageListener {

    private final List<MediaPostStorageListener> listeners;

    public CompositeMediaPostStorageListener(MediaPostRepository repository,
                                             StorageService storageService,
                                             ThreadPoolTaskScheduler scheduler,
                                             List<MediaPostStorageListener> listeners) {
        super(repository, storageService, scheduler);
        this.listeners = listeners;
    }

    @Override
    @Async
    @EventListener
    public void onNewMediaPostsAvailableEvent(NewMediaPostsAvailableEvent event) throws StorageException {
        Subscribable subscribable = event.getSubscribable();
        String bucketName = storageService.createBucketIfNotExists(getBucketNameForSubscribable(subscribable));
        log.info("Received event with {} mediaposts for subscribable {}", event.getMediaPosts().size(), subscribable.getHandle());
        for (MediaPostDto mediaPostDto : event.getMediaPosts()) {
            Optional<MediaPostStorageListener> matchingListener = listeners.stream()
                    .filter(listener -> listener.matches(mediaPostDto))
                    .findFirst();

            if (matchingListener.isPresent()) {
                MediaPostStorageListener listener = matchingListener.get();
                listener.onNewMediaPostAvailable(new NewSingleMediaPostAvailableEvent(subscribable, mediaPostDto, bucketName));
                continue;
            }

            this.onNewMediaPostAvailable(new NewSingleMediaPostAvailableEvent(subscribable, mediaPostDto, bucketName));
        }
    }

    @Override
    public void onNewMediaPostAvailable(NewSingleMediaPostAvailableEvent event) {
        uploadAndSaveMediaPost(event.getMediaPost(), event.getSubscribable(), event.getBucketName());
    }

    @Override
    public boolean matches(MediaPostDto mediaPostDto) {
        return true;
    }
}
