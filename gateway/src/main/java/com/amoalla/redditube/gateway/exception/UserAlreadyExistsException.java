package com.amoalla.redditube.gateway.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String userId) {
        super("User: " + userId + " already exists.");
    }
}
