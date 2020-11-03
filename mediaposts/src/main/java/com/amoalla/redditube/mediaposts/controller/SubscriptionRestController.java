package com.amoalla.redditube.mediaposts.controller;

import com.amoalla.redditube.client.model.MediaPost;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.Subscription;
import com.amoalla.redditube.mediaposts.service.SubscriptionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * * Follow
 * - Get my followed user/subreddit's posts (Feed)  GET /feed
 * - Follow users/subreddits                        POST /feed/followed
 * - Get followed users/subreddits                  GET /feed/followed
 * - Stop following users/subreddits                DELETE /feed/followed
 * <p>
 * * Organize
 * - Star a post (Add in starred folder)
 * - Create a new folder ...
 */
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionRestController {

    private final SubscriptionService subscriptionService;

    public SubscriptionRestController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/feed")
    public Flux<MediaPost> getFeed(Authentication authentication) {
        return subscriptionService.getFeed("");
    }

    @PostMapping
    public Mono<Subscription> subscribe(Subscribable subscribable, Authentication authentication) {
        return subscriptionService.subscribe(subscribable, "");
    }

    @DeleteMapping
    public Mono<Subscription> unsubscribe(Subscribable subscribable, Authentication authentication) {
        return subscriptionService.unsubscribe(subscribable, "");
    }

    @GetMapping
    public Flux<Subscription> getSubscriptions(Authentication authentication) {
        return subscriptionService.getSubscriptions("");
    }
}
