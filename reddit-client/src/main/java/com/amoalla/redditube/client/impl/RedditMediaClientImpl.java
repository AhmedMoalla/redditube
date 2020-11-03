package com.amoalla.redditube.client.impl;

import com.amoalla.redditube.client.RedditClient;
import com.amoalla.redditube.client.model.MediaPost;
import com.amoalla.redditube.client.model.Sort;
import reactor.core.publisher.Flux;

/**
 * Default RedditClient implementation. Uses MediaPostRequester to query reddit API
 */
public class RedditMediaClientImpl implements RedditClient {

    private final MediaPostRequester requester;

    public RedditMediaClientImpl(MediaPostRequester requester) {

        this.requester = requester;
    }

    @Override
    public Flux<MediaPost> getPosts(String usernameOrSubreddit, int limit) {
        return requester
                .limit(limit)
                .sendRequest(usernameOrSubreddit);
    }

    @Override
    public Flux<MediaPost> getPosts(String usernameOrSubreddit, Sort sort, int limit) {
        return requester
                .limit(limit)
                .sort(sort)
                .sendRequest(usernameOrSubreddit);
    }

    @Override
    public Flux<MediaPost> getPostsAfter(String usernameOrSubreddit, String postId, int limit) {
        return requester
                .after(postId)
                .limit(limit)
                .sendRequest(usernameOrSubreddit);
    }

    @Override
    public Flux<MediaPost> getPostsAfter(String usernameOrSubreddit, String postId, Sort sort, int limit) {
        return requester
                .after(postId)
                .limit(limit)
                .sort(sort)
                .sendRequest(usernameOrSubreddit);
    }

    @Override
    public Flux<MediaPost> getPostsBefore(String usernameOrSubreddit, String postId, int limit) {
        return requester
                .before(postId)
                .limit(limit)
                .sendRequest(usernameOrSubreddit);
    }

    @Override
    public Flux<MediaPost> getPostsBefore(String usernameOrSubreddit, String postId, Sort sort, int limit) {
        return requester
                .before(postId)
                .limit(limit)
                .sort(sort)
                .sendRequest(usernameOrSubreddit);
    }
}
