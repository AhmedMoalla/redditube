package com.amoalla.redditube.mediaposts.storage.cache.configuration;

import com.amoalla.redditube.mediaposts.repository.MediaPostRepository;
import com.amoalla.redditube.mediaposts.storage.cache.MediaHashCache;
import com.amoalla.redditube.mediaposts.storage.cache.impl.PersistentMediaHashCache;
import com.amoalla.redditube.mediaposts.storage.cache.impl.SynchronizedSetMediaHashCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "redditube.media-hash-cache.enabled", matchIfMissing = true)
@EnableConfigurationProperties(MediaHashCacheProperties.class)
public class MediaHashCacheConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "redditube.media-hash-cache.persistent", havingValue = "true")
    MediaHashCache providePersistentMediaHashCache(MediaPostRepository mediaPostRepository) {
        return new PersistentMediaHashCache(mediaPostRepository);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "redditube.media-hash-cache.persistent", havingValue = "false")
    MediaHashCache provideSynchronizedSetMediaHashCache() {
        return new SynchronizedSetMediaHashCache();
    }
}
