package com.amoalla.redditube.mediaposts.listener.impl;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.mediaposts.listener.NewSingleMediaPostAvailableEvent;
import com.amoalla.redditube.mediaposts.repository.MediaPostRepository;
import com.amoalla.redditube.mediaposts.storage.StorageService;
import com.amoalla.redditube.mediaposts.storage.cache.MediaHashCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GalleryMediaPostStorageListener extends AbstractMediaPostStorageListener {

    public GalleryMediaPostStorageListener(MediaPostRepository repository,
                                           StorageService storageService,
                                           ThreadPoolTaskScheduler scheduler,
                                           MediaHashCache mediaHashCache) {
        super(repository, storageService, scheduler, mediaHashCache);
    }

    @Override
    public void onNewMediaPostAvailable(NewSingleMediaPostAvailableEvent event) {
        log.debug("Gallery media post available {}", event);
        MediaPostDto mediaPostDto = event.getMediaPost();
        Map<String, String> mediaUrls = mediaPostDto.getGalleryMediaUrls();
        if (mediaUrls == null || mediaUrls.isEmpty()) {
            log.warn("Gallery Media Post: {} was skipped because gallery media urls was empty", mediaPostDto);
            return;
        }

        List<MediaPostDto> dtos = splitGalleryToMediaPosts(mediaPostDto, mediaUrls);
        dtos.forEach(dto -> uploadAndSaveMediaPost(dto, event.getSubscribable(), event.getBucketName(), event.getUploadCompletionCallback()));
    }

    private List<MediaPostDto> splitGalleryToMediaPosts(MediaPostDto parentDto, Map<String, String> mediaUrls) {
        return mediaUrls.entrySet()
                .stream()
                .map(entry -> {
                    MediaPostDto dto = parentDto.toBuilder().build();
                    dto.setId(entry.getKey());
                    dto.setMediaUrl(entry.getValue());
                    dto.setMediaThumbnailUrl(entry.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean matches(MediaPostDto mediaPostDto) {
        return mediaPostDto.isGallery();
    }

    @Override
    public int getNumberOfUploadTasks(MediaPostDto dto) {
        return dto.getGalleryMediaUrls().size();
    }
}
