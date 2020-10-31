package com.amoalla.redditube.mediaposts.service;

import com.amoalla.redditube.client.MediaPost;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.Subscription;
import com.amoalla.redditube.mediaposts.repository.SubscribableRepository;
import com.amoalla.redditube.mediaposts.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SubscriptionService {

    private final SubscribableRepository subscribableRepository;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscribableRepository subscribableRepository, SubscriptionRepository subscriptionRepository) {
        this.subscribableRepository = subscribableRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    public Flux<MediaPost> getFeed(String userId) {
        return Flux.just();
    }

    public Mono<Subscription> subscribe(Subscribable subscribable, String userId) {
        return Mono.just(null);
    }

    public Mono<Subscription> unsubscribe(Subscribable subscribable, String userId) {
        return Mono.just(null);
    }

    public Flux<Subscription> getSubscriptions(String userId) {
        return Flux.just();
    }

}
