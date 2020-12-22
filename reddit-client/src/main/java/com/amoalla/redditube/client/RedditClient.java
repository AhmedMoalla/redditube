package com.amoalla.redditube.client;

import com.amoalla.redditube.client.model.MediaPostDto;
import com.amoalla.redditube.client.model.Sort;
import reactor.core.publisher.Flux;

public interface RedditClient {

    int DEFAULT_LIMIT = 10;

    Flux<MediaPostDto> getPosts(String usernameOrSubreddit, int limit);
    Flux<MediaPostDto> getPosts(String usernameOrSubreddit, Sort sort, int limit);
    Flux<MediaPostDto> getPostsAfter(String usernameOrSubreddit, String postId, int limit);
    Flux<MediaPostDto> getPostsAfter(String usernameOrSubreddit, String postId, Sort sort, int limit);
    Flux<MediaPostDto> getPostsBefore(String usernameOrSubreddit, String postId, int limit);
    Flux<MediaPostDto> getPostsBefore(String usernameOrSubreddit, String postId, Sort sort, int limit);

    default Flux<MediaPostDto> getPosts(String usernameOrSubreddit) {
        return getPosts(usernameOrSubreddit, DEFAULT_LIMIT);
    }

    default Flux<MediaPostDto> getPosts(String usernameOrSubreddit, Sort sort) {
        return getPosts(usernameOrSubreddit, sort, DEFAULT_LIMIT);
    }

    default Flux<MediaPostDto> getPostsAfter(String usernameOrSubreddit, String postId) {
        return getPostsAfter(usernameOrSubreddit, postId, DEFAULT_LIMIT);
    }

    default Flux<MediaPostDto> getPostsAfter(String usernameOrSubreddit, String postId, Sort sort) {
        return getPostsAfter(usernameOrSubreddit, postId, sort, DEFAULT_LIMIT);
    }

    default Flux<MediaPostDto> getPostsBefore(String usernameOrSubreddit, String postId) {
        return getPostsBefore(usernameOrSubreddit, postId, DEFAULT_LIMIT);
    }

    default Flux<MediaPostDto> getPostsBefore(String usernameOrSubreddit, String postId, Sort sort) {
        return getPostsBefore(usernameOrSubreddit, postId, sort, DEFAULT_LIMIT);
    }
}
