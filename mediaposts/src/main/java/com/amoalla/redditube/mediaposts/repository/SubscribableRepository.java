package com.amoalla.redditube.mediaposts.repository;

import com.amoalla.redditube.mediaposts.entity.Subscribable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscribableRepository extends CrudRepository<Subscribable, String> {
    @Query("SELECT COUNT(s.subscriptions) FROM Subscribable s WHERE s.id = :subscribableId")
    long countSubscriptionsById(@Param("subscribableId") String subscribableId);
}
