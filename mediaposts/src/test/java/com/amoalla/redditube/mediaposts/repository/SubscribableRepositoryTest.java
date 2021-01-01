package com.amoalla.redditube.mediaposts.repository;

import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.SubscribableType;
import com.amoalla.redditube.mediaposts.entity.Subscription;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = "spring.liquibase.enabled=false")
class SubscribableRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubscribableRepository subscribableRepository;

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

        subscription = new Subscription();
        subscription.setUsername("USERNAME1");
        subscription.setSubscribable(subscribable);
        entityManager.persist(subscription);

        long count = subscribableRepository.countSubscriptionsById(subscribable.getId());
        assertEquals(2, count);
    }
}