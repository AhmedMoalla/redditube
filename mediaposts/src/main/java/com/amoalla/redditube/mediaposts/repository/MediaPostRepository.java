package com.amoalla.redditube.mediaposts.repository;

import com.amoalla.redditube.mediaposts.entity.MediaPost;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MediaPostRepository extends CrudRepository<MediaPost, String> {
    Optional<MediaPost> findByHash(String hash);
    @Query("SELECT m.hash FROM MediaPost m")
    Set<String> findAllHashes();
}
