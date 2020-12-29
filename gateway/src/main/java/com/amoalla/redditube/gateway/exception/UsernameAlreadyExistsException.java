package com.amoalla.redditube.gateway.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Username: " + username + " already exists.");
    }
}
