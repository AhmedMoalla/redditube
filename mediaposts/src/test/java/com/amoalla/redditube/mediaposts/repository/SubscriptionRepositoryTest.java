package com.amoalla.redditube.mediaposts.repository;

import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.SubscribableType;
import com.amoalla.redditube.mediaposts.entity.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "spring.liquibase.enabled=false")
class SubscriptionRepositoryTest {

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_HANDLE = "TEST_HANDLE";
    private static final SubscribableType TEST_TYPE = SubscribableType.USER;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private Subscribable testSubscribable;
    private Subscription testSubscription;

    @BeforeEach
    void setUp() {
        testSubscribable = new Subscribable();
        testSubscribable.setHandle(TEST_HANDLE);
        testSubscribable.setType(TEST_TYPE);
        testSubscribable = entityManager.persist(testSubscribable);

        testSubscription = new Subscription();
        testSubscription.setUsername(TEST_USERNAME);
        testSubscription.setSubscribable(testSubscribable);
        entityManager.persist(testSubscription);
    }

    @Test
    void testFindAllByUsername() {
        long count = subscriptionRepository.findAllByUsername(TEST_USERNAME).size();
        assertEquals(1, count);
    }

    @Test
    void testExistsByUsernameAndSubscribableId() {
        assertTrue(subscriptionRepository.existsByUsernameAndSubscribableId(TEST_USERNAME, testSubscribable.getId()));
    }

    @Test
    void testFindByUsernameAndSubscribableId() {
        Optional<Subscription> subscription = subscriptionRepository.findByUsernameAndSubscribableId(TEST_USERNAME, testSubscribable.getId());
        assertTrue(subscription.isPresent());
        assertEquals(testSubscription, subscription.get());
        // Check that subscribable in subscription is fetched before deletion
        entityManager.remove(testSubscription);
        assertEquals(testSubscribable, subscription.get().getSubscribable());
    }
}