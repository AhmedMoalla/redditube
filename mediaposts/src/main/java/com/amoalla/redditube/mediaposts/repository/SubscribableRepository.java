package com.amoalla.redditube.mediaposts.repository;

import com.amoalla.redditube.mediaposts.entity.Subscribable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscribableRepository extends ReactiveCrudRepository<String, Subscribable> {
}
