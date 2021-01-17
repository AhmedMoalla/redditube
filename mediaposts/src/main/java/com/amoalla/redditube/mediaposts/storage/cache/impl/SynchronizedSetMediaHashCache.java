package com.amoalla.redditube.mediaposts.storage.cache.impl;

import com.amoalla.redditube.mediaposts.storage.cache.MediaHashCache;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SynchronizedSetMediaHashCache implements MediaHashCache {

    protected final Set<String> hashCache = Collections.synchronizedSet(new HashSet<>());

    @Override
    public boolean exists(String hash) {
        return hashCache.contains(hash);
    }

    @Override
    public void add(String hash) {
        Assert.isTrue(!exists(hash), "This cache does not support duplicate values");
        hashCache.add(hash);
    }
}
