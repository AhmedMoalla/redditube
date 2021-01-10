package com.amoalla.redditube.api.service;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.dto.Sort;
import reactor.core.publisher.Flux;

public interface ExplorerService {
    Flux<MediaPostDto> getPosts(String usernameOrSubreddit, String after, String before, int limit);
    Flux<MediaPostDto> getPosts(String usernameOrSubreddit, String after, String before, int limit, Sort sort);
}
