package com.amoalla.redditube.mediaposts.exception;

public class AlreadyUnsubscribedException extends RuntimeException {

    public AlreadyUnsubscribedException(String username, String subscribableHandle) {
        super(String.format("User %s is already not subscribed to %s", username, subscribableHandle));
    }
}
