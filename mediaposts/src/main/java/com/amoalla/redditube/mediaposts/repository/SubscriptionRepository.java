package com.amoalla.redditube.mediaposts.repository;

import com.amoalla.redditube.mediaposts.entity.Subscription;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends ReactiveCrudRepository<Integer, Subscription> {
}
