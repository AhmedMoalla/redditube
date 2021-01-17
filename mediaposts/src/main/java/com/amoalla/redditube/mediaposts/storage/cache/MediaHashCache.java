package com.amoalla.redditube.mediaposts.storage.cache;

public interface MediaHashCache {
    boolean exists(String hash);
    void add(String hash);
}
