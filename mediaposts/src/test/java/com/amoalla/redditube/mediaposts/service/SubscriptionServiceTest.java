package com.amoalla.redditube.mediaposts.service;

import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.SubscribableType;
import com.amoalla.redditube.mediaposts.entity.Subscription;
import com.amoalla.redditube.mediaposts.exception.AlreadySubscribedException;
import com.amoalla.redditube.mediaposts.exception.AlreadyUnsubscribedException;
import com.amoalla.redditube.mediaposts.repository.SubscribableRepository;
import com.amoalla.redditube.mediaposts.repository.SubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SubscriptionService.class)
class SubscriptionServiceTest {

    @MockBean
    private SubscribableRepository subscribableRepository;

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    private Subscribable testSubscribable;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {

        testSubscribable = new Subscribable();
        testSubscribable.setHandle("HANDLE");
        testSubscribable.setType(SubscribableType.USER);

        testSubscription = new Subscription();
        testSubscription.setUsername("username");
        testSubscription.setSubscribable(testSubscribable);
    }

    @Test
    @Disabled("Not implemented yet.")
    void testGetFeed() {

        subscriptionService.getFeed("");
        assertTrue(true);
    }

    @Test
    void testSubscribe() {

        when(subscriptionRepository.existsById(Mockito.any())).thenReturn(false);
        when(subscriptionRepository.save(Mockito.any())).thenReturn(testSubscription);
        when(subscribableRepository.existsById(Mockito.any())).thenReturn(false);
        Mono<Subscription> returnedSubscription = subscriptionService.subscribe(testSubscribable, "username");

        verify(subscribableRepository).save(testSubscribable);
        verify(subscriptionRepository).save(testSubscription);
        StepVerifier.create(returnedSubscription)
                .expectNext(testSubscription)
                .expectComplete()
                .verify();
    }

    @Test
    void testSubscribeWhenAlreadySubscribed() {

        when(subscriptionRepository.existsById(Mockito.any())).thenReturn(true);
        assertThrows(AlreadySubscribedException.class, () -> subscriptionService.subscribe(testSubscribable, "username"));
    }

    @Test
    void testUnsubscribe() {

        when(subscriptionRepository.findById(Mockito.any())).thenReturn(Optional.of(testSubscription));
        when(subscribableRepository.findById(Mockito.any())).thenReturn(Optional.of(testSubscribable));
        when(subscribableRepository.countSubscriptionsById(Mockito.any())).thenReturn(0L);
        Mono<Subscription> returnedSubscription = subscriptionService.unsubscribe(testSubscribable, "username");

        verify(subscriptionRepository).delete(testSubscription);
        verify(subscribableRepository).delete(testSubscribable);
        StepVerifier.create(returnedSubscription)
                .expectNext(testSubscription)
                .expectComplete()
                .verify();
    }

    @Test
    void testUnsubscribeWhenAlreadyUnsubscribed() {

        when(subscriptionRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        assertThrows(AlreadyUnsubscribedException.class, () -> subscriptionService.unsubscribe(testSubscribable, "username"));
    }

    @Test
    void testGetSubscriptions() {

        when(subscriptionRepository.findAllByUsername("username")).thenReturn(Collections.singletonList(testSubscription));
        Flux<Subscribable> returnedSubscriptions = subscriptionService.getSubscriptions("username");

        StepVerifier.create(returnedSubscriptions)
                .expectNext(testSubscribable)
                .expectComplete()
                .verify();
    }

}