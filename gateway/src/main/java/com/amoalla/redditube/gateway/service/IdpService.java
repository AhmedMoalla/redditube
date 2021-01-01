package com.amoalla.redditube.gateway.service;

import com.amoalla.redditube.gateway.entity.RedditubeUser;
import reactor.core.publisher.Mono;

/**
 * Abstraction to propagate user data to an Identity Provider
 */
public interface IdpService {

    Mono<RedditubeUser> registerUser(RedditubeUser user, String rawPassword);
}
