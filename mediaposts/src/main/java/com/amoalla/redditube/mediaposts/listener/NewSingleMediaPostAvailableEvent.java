package com.amoalla.redditube.mediaposts.listener;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import lombok.Getter;

public class NewSingleMediaPostAvailableEvent {
    @Getter
    private final MediaPostDto mediaPost;
    @Getter
    private final Subscribable subscribable;
    @Getter
    private final String bucketName;

    public NewSingleMediaPostAvailableEvent(Subscribable subscribable, MediaPostDto mediaPost, String bucketName) {
        this.mediaPost = mediaPost;
        this.subscribable = subscribable;
        this.bucketName = bucketName;
    }
}
