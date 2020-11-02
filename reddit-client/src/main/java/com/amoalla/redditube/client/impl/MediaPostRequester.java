package com.amoalla.redditube.client.impl;

import com.amoalla.redditube.client.model.MediaPost;
import com.amoalla.redditube.client.model.MediaPostListings;
import com.amoalla.redditube.client.model.Sort;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;

import static com.amoalla.redditube.client.RedditClient.DEFAULT_LIMIT;

/**
 * Wrapper for WebClient to have a nice fluent API when sending requests
 */
@RequiredArgsConstructor
public class MediaPostRequester {

    public static final String USER_POSTS_URI = "/user/{username}/submitted";
    public static final String SUBREDDIT_POSTS_URI = "/r/{subreddit}/{sort}";
    private static final int MAX_LIMIT = 100;
    private static final String REQUESTED_TYPE = "links";

    private static final class QueryParams {
        public static final String LIMIT = "limit";
        public static final String AFTER = "after";
        public static final String BEFORE = "before";
        public static final String COUNT = "count";
        public static final String SORT = "sort";
        public static final String TYPE = "type";
    }

    @Getter
    @RequiredArgsConstructor
    public enum Type {
        USER(USER_POSTS_URI),
        SUBREDDIT(SUBREDDIT_POSTS_URI);

        private final String uri;
    }

    private final Type type;
    private final WebClient webClient;

    private int limit = 10;
    private String after = "";
    private String before = "";
    private String count = "";
    private Sort sort = Sort.NEW;

    public MediaPostRequester limit(int limit) {
        this.limit = Math.max(limit, DEFAULT_LIMIT);
        this.limit = Math.min(this.limit, MAX_LIMIT);
        return this;
    }

    public MediaPostRequester after(String after) {
        this.after = after;
        return this;
    }

    public MediaPostRequester before(String before) {
        this.before = before;
        return this;
    }

    public MediaPostRequester count(int count) {
        int safeCount = Math.max(count, DEFAULT_LIMIT);
        safeCount = Math.min(safeCount, MAX_LIMIT);
        this.count = Integer.toString(safeCount);
        return this;
    }

    public MediaPostRequester sort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public Flux<MediaPost> sendRequest(String usernameOrSubreddit) {
        return webClient
                .get()
                .uri(builder -> buildURI(builder, usernameOrSubreddit))
                .retrieve()
                .bodyToMono(MediaPostListings.class)
                .flatMapIterable(MediaPostListings::getMediaPosts);
    }

    private URI buildURI(UriBuilder builder, String usernameOrSubreddit) {
        builder = builder.path(type.getUri())
                .queryParam(QueryParams.LIMIT, limit)
                .queryParam(QueryParams.AFTER, after)
                .queryParam(QueryParams.BEFORE, before)
                .queryParam(QueryParams.COUNT, count)
                .queryParam(QueryParams.TYPE, REQUESTED_TYPE);

        if (Type.USER.equals(type)) {
            builder.queryParam(QueryParams.SORT, sort.name().toLowerCase());
        } else if (Type.SUBREDDIT.equals(type)) {
            return builder.build(usernameOrSubreddit, sort.name().toLowerCase());
        }

        return builder.build(usernameOrSubreddit);
    }

}