package com.amoalla.redditube.mediaposts.repository;

import com.amoalla.redditube.mediaposts.entity.Subscription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends CrudRepository<Subscription, Integer> {
    List<Subscription> findAllByUsername(@NotNull String username);
    boolean existsByUsernameAndSubscribableId(@NotNull String username, Integer subscribableId);
    @Query("SELECT s FROM Subscription s JOIN FETCH s.subscribable WHERE s.username = :username AND s.subscribable.id = :subscribableId")
    Optional<Subscription> findByUsernameAndSubscribableId(@Param("username") @NotNull String username,
                                                           @Param("subscribableId") Integer subscribableId);
}
