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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = "spring.liquibase.enabled=false")
class SubscribableRepositoryTest {

    private static final String TEST_HANDLE = "TEST_HANDLE";
    private static final SubscribableType TEST_TYPE = SubscribableType.USER;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubscribableRepository subscribableRepository;

    private Subscribable testSubscribable;

    @BeforeEach
    void setUp() {
        testSubscribable = new Subscribable();
        testSubscribable.setHandle(TEST_HANDLE);
        testSubscribable.setType(TEST_TYPE);
        testSubscribable = entityManager.persist(testSubscribable);

        Subscription subscription = new Subscription();
        subscription.setUsername("USERNAME");
        subscription.setSubscribable(testSubscribable);
        entityManager.persist(subscription);

        subscription = new Subscription();
        subscription.setUsername("USERNAME1");
        subscription.setSubscribable(testSubscribable);
        entityManager.persist(subscription);
    }

    @Test
    void testCountSubscriptionsById() {
        long count = subscribableRepository.countSubscriptionsById(testSubscribable.getId());
        assertEquals(2, count);
    }

    @Test
    void testExistsByHandleAndType() {
        assertTrue(subscribableRepository.existsByHandleAndType(TEST_HANDLE, TEST_TYPE));
    }

    @Test
    void testFindByHandleAndType() {
        Optional<Subscribable> subscribable = subscribableRepository.findByHandleAndType(TEST_HANDLE, TEST_TYPE);
        assertTrue(subscribable.isPresent());
        assertEquals(testSubscribable, subscribable.get());
    }
}