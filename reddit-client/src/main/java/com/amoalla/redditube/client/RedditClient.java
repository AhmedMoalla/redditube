package com.amoalla.redditube.client;

import reactor.core.publisher.Flux;

public interface RedditClient {

    int DEFAULT_LIMIT = 10;

    Flux<MediaPost> getPosts(String usernameOrSubreddit, int limit);
    Flux<MediaPost> getPosts(String usernameOrSubreddit, Sort sort, int limit);
    Flux<MediaPost> getPostsAfter(String usernameOrSubreddit, String postId, int limit);
    Flux<MediaPost> getPostsAfter(String usernameOrSubreddit, String postId, Sort sort, int limit);
    Flux<MediaPost> getPostsBefore(String usernameOrSubreddit, String postId, int limit);
    Flux<MediaPost> getPostsBefore(String usernameOrSubreddit, String postId, Sort sort, int limit);

    default Flux<MediaPost> getPosts(String usernameOrSubreddit) {
        return getPosts(usernameOrSubreddit, DEFAULT_LIMIT);
    }

    default Flux<MediaPost> getPosts(String usernameOrSubreddit, Sort sort) {
        return getPosts(usernameOrSubreddit, sort, DEFAULT_LIMIT);
    }

    default Flux<MediaPost> getPostsAfter(String usernameOrSubreddit, String postId) {
        return getPostsAfter(usernameOrSubreddit, postId, DEFAULT_LIMIT);
    }

    default Flux<MediaPost> getPostsAfter(String usernameOrSubreddit, String postId, Sort sort) {
        return getPostsAfter(usernameOrSubreddit, postId, sort, DEFAULT_LIMIT);
    }

    default Flux<MediaPost> getPostsBefore(String usernameOrSubreddit, String postId) {
        return getPostsBefore(usernameOrSubreddit, postId, DEFAULT_LIMIT);
    }

    default Flux<MediaPost> getPostsBefore(String usernameOrSubreddit, String postId, Sort sort) {
        return getPostsBefore(usernameOrSubreddit, postId, sort, DEFAULT_LIMIT);
    }
}
