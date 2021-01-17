package com.amoalla.redditube.mediaposts.storage.cache.impl;

import com.amoalla.redditube.mediaposts.repository.MediaPostRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PersistentMediaHashCacheTest {

    @Test
    void testCache() {
        MediaPostRepository repository = Mockito.mock(MediaPostRepository.class);
        PersistentMediaHashCache cache = new PersistentMediaHashCache(repository);
        cache.afterPropertiesSet();
        Mockito.verify(repository).findAllHashes();
    }
}