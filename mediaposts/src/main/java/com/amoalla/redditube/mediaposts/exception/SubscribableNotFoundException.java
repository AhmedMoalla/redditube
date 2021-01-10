package com.amoalla.redditube.mediaposts.exception;

public class SubscribableNotFoundException extends RuntimeException {
    public SubscribableNotFoundException(String subscribableHandle) {
        super(String.format("Subscribable %s was not found.", subscribableHandle));
    }
}
