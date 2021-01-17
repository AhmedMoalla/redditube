package com.amoalla.redditube.mediaposts.listener.impl;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.mediaposts.entity.MediaPost;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.listener.NewSingleMediaPostAvailableEvent;
import com.amoalla.redditube.mediaposts.listener.resolver.EmbedUrlResolver;
import com.amoalla.redditube.mediaposts.listener.resolver.ResolverMediaUrls;
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
public class EmbedMediaPostStorageListener extends AbstractMediaPostStorageListener {

    private final Map<String, EmbedUrlResolver> urlResolvers;

    public EmbedMediaPostStorageListener(MediaPostRepository repository,
                                         StorageService storageService,
                                         ThreadPoolTaskScheduler scheduler,
                                         MediaHashCache mediaHashCache,
                                         List<EmbedUrlResolver> urlResolvers) {
        super(repository, storageService, scheduler, mediaHashCache);
        this.urlResolvers = urlResolvers.stream()
                .collect(Collectors.toMap(EmbedUrlResolver::getProviderName, embedUrlResolver -> embedUrlResolver));
    }

    @Override
    public void onNewMediaPostAvailable(NewSingleMediaPostAvailableEvent event) {
        log.debug("Embed media post available {}", event);
        MediaPostDto mediaPostDto = event.getMediaPost();
        String providerName = mediaPostDto.getEmbedProviderName();
        EmbedUrlResolver urlResolver = urlResolvers.get(providerName);
        if (urlResolver == null) {
            log.warn("Embed Media Post {} was skipped because no urlResolver was found for provider: {}", mediaPostDto, providerName);
            return;
        }

        String embedHtml = mediaPostDto.getEmbedHtml();
        ResolverMediaUrls mediaUrls = urlResolver.resolve(embedHtml);

        if (mediaUrls != null) {
            mediaPostDto.setMediaUrl(mediaUrls.getMediaUrl());
            mediaPostDto.setMediaThumbnailUrl(mediaUrls.getMediaThumbnailUrl());
            uploadAndSaveMediaPost(mediaPostDto, event.getSubscribable(), event.getBucketName(), event.getUploadCompletionCallback());
        }
    }

    @Override
    protected MediaPost mapToEntity(MediaPostDto mediaPostDto, Subscribable subscribable) {
        MediaPost mediaPost = super.mapToEntity(mediaPostDto, subscribable);
        mediaPost.setIsVideo(mediaPostDto.isEmbed());
        return mediaPost;
    }

    @Override
    public boolean matches(MediaPostDto mediaPostDto) {
        return mediaPostDto.isEmbed();
    }
}
