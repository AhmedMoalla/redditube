package com.amoalla.redditube.mediaposts.exception;

public class AlreadySubscribedException extends RuntimeException {

    public AlreadySubscribedException(String username, String subscribableId) {
        super(String.format("User %s is already subscribed to %s", username, subscribableId));
    }
}
