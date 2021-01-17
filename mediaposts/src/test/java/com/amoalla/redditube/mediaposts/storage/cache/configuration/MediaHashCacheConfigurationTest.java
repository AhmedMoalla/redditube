package com.amoalla.redditube.mediaposts.storage.cache.configuration;

import com.amoalla.redditube.mediaposts.repository.MediaPostRepository;
import com.amoalla.redditube.mediaposts.storage.cache.MediaHashCache;
import com.amoalla.redditube.mediaposts.storage.cache.impl.PersistentMediaHashCache;
import com.amoalla.redditube.mediaposts.storage.cache.impl.SynchronizedSetMediaHashCache;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaHashCacheConfigurationTest {

    @Test
    void testProvidesInMemoryCache() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(MediaHashCacheConfiguration.class))
                .withPropertyValues(
                        "redditube.media-hash-cache.enabled=true",
                        "redditube.media-hash-cache.persistent=false")
                .run(context -> {
                    assertThat(context).hasSingleBean(MediaHashCache.class);
                    MediaHashCache cache = context.getBean(MediaHashCache.class);
                    assertTrue(cache instanceof SynchronizedSetMediaHashCache);
                });
    }

    @Test
    void testProvidesPersistentCache() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(MediaHashCacheConfiguration.class))
                .withUserConfiguration(ProvideBeansForTest.class)
                .withPropertyValues(
                        "redditube.media-hash-cache.enabled=true",
                        "redditube.media-hash-cache.persistent=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(MediaHashCache.class);
                    MediaHashCache cache = context.getBean(MediaHashCache.class);
                    assertTrue(cache instanceof PersistentMediaHashCache);
                });
    }

    @TestConfiguration
    static class ProvideBeansForTest {
        @Bean
        MediaPostRepository provideMediaPostRepository() {
            return Mockito.mock(MediaPostRepository.class);
        }
    }

}