package com.amoalla.redditube.mediaposts.repository;

import com.amoalla.redditube.mediaposts.entity.MediaPost;
import org.springframework.data.repository.CrudRepository;

public interface MediaPostRepository extends CrudRepository<MediaPost, Integer> {
}
