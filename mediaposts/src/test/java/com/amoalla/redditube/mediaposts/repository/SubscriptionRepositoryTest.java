package com.amoalla.redditube.mediaposts.repository;

import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.SubscribableType;
import com.amoalla.redditube.mediaposts.entity.Subscription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "spring.liquibase.enabled=false")
class SubscriptionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Test
    void testCountSubscriptionsById() {
        Subscribable subscribable = new Subscribable();
        subscribable.setId("ID");
        subscribable.setType(SubscribableType.USER);
        subscribable = entityManager.persist(subscribable);

        Subscription subscription = new Subscription();
        subscription.setUsername("USERNAME");
        subscription.setSubscribable(subscribable);
        entityManager.persist(subscription);

        subscribable = new Subscribable();
        subscribable.setId("ID2");
        subscribable.setType(SubscribableType.USER);
        subscribable = entityManager.persist(subscribable);

        subscription = new Subscription();
        subscription.setUsername("USERNAME");
        subscription.setSubscribable(subscribable);
        entityManager.persist(subscription);

        long count = subscriptionRepository.findAllByUsername("USERNAME").size();
        assertEquals(2, count);
    }
}