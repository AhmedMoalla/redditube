package com.amoalla.redditube.mediaposts.controller;

import com.amoalla.redditube.client.model.MediaPost;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.Subscription;
import com.amoalla.redditube.mediaposts.service.SubscriptionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
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
    public Flux<MediaPost> getFeed(@AuthenticationPrincipal String userId) {
        return subscriptionService.getFeed(userId);
    }

    @PostMapping
    public Mono<Subscription> subscribe(Subscribable subscribable, @AuthenticationPrincipal String userId) {
        return subscriptionService.subscribe(subscribable, userId);
    }

    @DeleteMapping
    public Mono<Subscription> unsubscribe(Subscribable subscribable, @AuthenticationPrincipal String userId) {
        return subscriptionService.unsubscribe(subscribable, userId);
    }

    @GetMapping
    public Flux<Subscription> getSubscriptions(@AuthenticationPrincipal String userId) {
        return subscriptionService.getSubscriptions(userId);
    }
}
