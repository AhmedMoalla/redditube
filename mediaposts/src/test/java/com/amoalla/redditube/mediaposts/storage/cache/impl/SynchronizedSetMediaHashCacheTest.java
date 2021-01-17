package com.amoalla.redditube.mediaposts.storage.cache.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SynchronizedSetMediaHashCacheTest {

    @Test
    void testCache() {
        SynchronizedSetMediaHashCache cache = new SynchronizedSetMediaHashCache();
        cache.add("hash");
        assertTrue(cache.exists("hash"));
        assertFalse(cache.exists("do_not_exist"));
    }

}