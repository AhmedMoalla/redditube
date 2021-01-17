package com.amoalla.redditube.mediaposts.listener;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.mediaposts.scraper.event.NewMediaPostsAvailableEvent;
import com.amoalla.redditube.mediaposts.storage.exception.StorageException;

public interface MediaPostStorageListener {
    void onNewMediaPostsAvailableEvent(NewMediaPostsAvailableEvent event) throws StorageException;
    void onNewMediaPostAvailable(NewSingleMediaPostAvailableEvent event);
    boolean matches(MediaPostDto mediaPostDto);
    default int getNumberOfUploadTasks(MediaPostDto dto) {
        return 1;
    }
}
