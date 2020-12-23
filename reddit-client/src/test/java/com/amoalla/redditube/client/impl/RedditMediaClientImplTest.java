package com.amoalla.redditube.client.impl;

import com.amoalla.redditube.client.RedditClient;
import com.amoalla.redditube.client.model.MediaPostDto;
import com.amoalla.redditube.client.model.Sort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class RedditMediaClientImplTest {

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final Sort TEST_SORT = Sort.NEW;
    private static final int TEST_LIMIT = 25;
    private static final String TEST_POST_ID = "TEST_POST_ID";

    @Mock
    private MediaPostRequester mediaPostRequester;

    @InjectMocks
    private final RedditClient redditClient = new RedditMediaClientImpl();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mediaPostRequester.sendRequest(anyString())).thenReturn(Flux.just());
        when(mediaPostRequester.limit(anyInt())).thenCallRealMethod();
        when(mediaPostRequester.sort(TEST_SORT)).thenCallRealMethod();
        when(mediaPostRequester.after(TEST_POST_ID)).thenCallRealMethod();
        when(mediaPostRequester.before(TEST_POST_ID)).thenCallRealMethod();
    }

    @Test
    void testGetPostsWithUsernameOnly() {
        testGetPosts(redditClient.getPosts(TEST_USERNAME));
    }

    @Test
    void testGetPostsWithUsernameAndSort() {
        testGetPosts(redditClient.getPosts(TEST_USERNAME, TEST_SORT));
        verify(mediaPostRequester).sort(TEST_SORT);
    }

    @Test
    void testGetPostsWithUsernameAndLimit() {
        testGetPosts(redditClient.getPosts(TEST_USERNAME, TEST_LIMIT), TEST_LIMIT);
    }

    @Test
    void testGetPostsWithUsernameAndSortAndLimit() {
        testGetPosts(redditClient.getPosts(TEST_USERNAME, TEST_SORT, TEST_LIMIT), TEST_LIMIT);
        verify(mediaPostRequester).sort(TEST_SORT);
    }

    @Test
    void testGetPostsAfterWithUsernameAndPostId() {
        testGetPosts(redditClient.getPostsAfter(TEST_USERNAME, TEST_POST_ID));
        verify(mediaPostRequester).after(TEST_POST_ID);
    }

    @Test
    void testGetPostsAfterWithUsernameAndPostIdAndSort() {
        testGetPosts(redditClient.getPostsAfter(TEST_USERNAME, TEST_POST_ID, TEST_SORT));
        verify(mediaPostRequester).after(TEST_POST_ID);
        verify(mediaPostRequester).sort(TEST_SORT);
    }

    @Test
    void testGetPostsAfterWithUsernameAndPostIdAndLimit() {
        testGetPosts(redditClient.getPostsAfter(TEST_USERNAME, TEST_POST_ID, TEST_LIMIT), TEST_LIMIT);
        verify(mediaPostRequester).after(TEST_POST_ID);
    }

    @Test
    void testGetPostsAfterWithUsernameAndPostIdAndSortAndLimit() {
        testGetPosts(redditClient.getPostsAfter(TEST_USERNAME, TEST_POST_ID, TEST_SORT, TEST_LIMIT), TEST_LIMIT);
        verify(mediaPostRequester).after(TEST_POST_ID);
        verify(mediaPostRequester).sort(TEST_SORT);
    }

    @Test
    void testGetPostsBeforeWithUsernameAndPostId() {
        testGetPosts(redditClient.getPostsBefore(TEST_USERNAME, TEST_POST_ID));
        verify(mediaPostRequester).before(TEST_POST_ID);
    }

    @Test
    void testGetPostsBeforeWithUsernameAndPostIdAndSort() {
        testGetPosts(redditClient.getPostsBefore(TEST_USERNAME, TEST_POST_ID, TEST_SORT));
        verify(mediaPostRequester).before(TEST_POST_ID);
        verify(mediaPostRequester).sort(TEST_SORT);
    }

    @Test
    void testGetPostsBeforeWithUsernameAndPostIdAndLimit() {
        testGetPosts(redditClient.getPostsBefore(TEST_USERNAME, TEST_POST_ID, TEST_LIMIT), TEST_LIMIT);
        verify(mediaPostRequester).before(TEST_POST_ID);
    }

    @Test
    void testGetPostsBeforeWithUsernameAndPostIdAndSortAndLimit() {
        testGetPosts(redditClient.getPostsBefore(TEST_USERNAME, TEST_POST_ID, TEST_SORT, TEST_LIMIT), TEST_LIMIT);
        verify(mediaPostRequester).before(TEST_POST_ID);
        verify(mediaPostRequester).sort(TEST_SORT);
    }

    void testGetPosts(Flux<MediaPostDto> posts) {
        testGetPosts(posts, RedditClient.DEFAULT_LIMIT);
    }

    void testGetPosts(Flux<MediaPostDto> posts, int limit) {
        StepVerifier.create(posts)
                .expectComplete()
                .verify();

        verify(mediaPostRequester).limit(limit);
        verify(mediaPostRequester).sendRequest(TEST_USERNAME);
    }
}