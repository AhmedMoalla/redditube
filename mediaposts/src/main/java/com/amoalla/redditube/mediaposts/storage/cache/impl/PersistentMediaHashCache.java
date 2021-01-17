package com.amoalla.redditube.mediaposts.storage.cache.impl;

import com.amoalla.redditube.mediaposts.repository.MediaPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
public class PersistentMediaHashCache extends SynchronizedSetMediaHashCache implements InitializingBean {

    private final MediaPostRepository mediaPostRepository;

    public PersistentMediaHashCache(MediaPostRepository mediaPostRepository) {
        this.mediaPostRepository = mediaPostRepository;
    }

    @Override
    public void afterPropertiesSet() {
        log.info("Loading media hashes in cache...");
        hashCache.addAll(mediaPostRepository.findAllHashes());
        log.info("Finished loading media hashes.");
    }
}
