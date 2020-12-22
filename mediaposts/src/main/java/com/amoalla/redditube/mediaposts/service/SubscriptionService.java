package com.amoalla.redditube.mediaposts.service;

import com.amoalla.redditube.client.model.MediaPostDto;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.Subscription;
import com.amoalla.redditube.mediaposts.entity.SubscriptionID;
import com.amoalla.redditube.mediaposts.exception.AlreadySubscribedException;
import com.amoalla.redditube.mediaposts.exception.AlreadyUnsubscribedException;
import com.amoalla.redditube.mediaposts.repository.SubscribableRepository;
import com.amoalla.redditube.mediaposts.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class SubscriptionService {

    private final SubscribableRepository subscribableRepository;
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscribableRepository subscribableRepository, SubscriptionRepository subscriptionRepository) {
        this.subscribableRepository = subscribableRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    // TODO Implement get feed
    public Flux<MediaPostDto> getFeed(String username) {
        log.warn("SubscriptionService.getFeed({}) Not implemented yet. Returning empty feed.", username);
        return Flux.fromArray(new MediaPostDto[0]);
    }

    public Mono<Subscription> subscribe(Subscribable subscribable, String username) {
        Subscription newSubscription = new Subscription();
        newSubscription.setUsername(username);

        Optional<Subscription> subscription = subscriptionRepository.findById(new SubscriptionID(username, subscribable));
        if (subscription.isPresent()) {
            throw new AlreadySubscribedException(username, subscribable.getId());
        }

        if (!subscribableRepository.existsById(subscribable.getId())) {
            subscribableRepository.save(subscribable);
        }

        newSubscription.setSubscribable(subscribable);

        return Mono.just(subscriptionRepository.save(newSubscription));
    }

    public Mono<Subscription> unsubscribe(Subscribable subscribable, String username) {
        Optional<Subscription> subscription = subscriptionRepository.findById(new SubscriptionID(username, subscribable));
        if (subscription.isEmpty()) {
            throw new AlreadyUnsubscribedException(username, subscribable.getId());
        }

        subscriptionRepository.delete(subscription.get());

        Optional<Subscribable> foundSubscribable = subscribableRepository.findById(subscribable.getId());
        if (foundSubscribable.isPresent()
                && subscribableRepository.countSubscriptionsById(foundSubscribable.get().getId()) == 0) {

            subscribableRepository.delete(foundSubscribable.get());
        }

        return Mono.just(subscription.get());
    }

    public Flux<Subscribable> getSubscriptions(String username) {
        return Flux.fromIterable(subscriptionRepository.findAllByUsername(username))
                .map(Subscription::getSubscribable);
    }

}
