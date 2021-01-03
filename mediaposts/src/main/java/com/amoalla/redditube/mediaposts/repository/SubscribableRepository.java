package com.amoalla.redditube.mediaposts.repository;

import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.SubscribableType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface SubscribableRepository extends CrudRepository<Subscribable, Integer> {
    @Query("SELECT SIZE(s.subscriptions) FROM Subscribable s WHERE s.id = :subscribableId")
    long countSubscriptionsById(@Param("subscribableId") Integer subscribableId);
    boolean existsByHandleAndType(@NotNull String handle, @NotNull SubscribableType type);
    Optional<Subscribable> findByHandleAndType(@NotNull String handle, @NotNull SubscribableType type);
}
