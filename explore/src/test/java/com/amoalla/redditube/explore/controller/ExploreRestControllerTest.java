package com.amoalla.redditube.explore.controller;

import com.amoalla.redditube.client.RedditClient;
import com.amoalla.redditube.client.model.Sort;
import com.amoalla.redditube.commons.CommonsConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.verify;

@WebFluxTest
@ActiveProfiles("default")
@ContextConfiguration(classes = {CommonsConfiguration.class, ExploreRestController.class})
class ExploreRestControllerTest {

    private static final String GET_POSTS_PATH = "/{usernameOrSubreddit}";
    private static final String GET_POSTS_WITH_SORTING_PATH = GET_POSTS_PATH + "/{sort}";
    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_POST_ID = "TEST_POST_ID";
    private static final Sort TEST_SORT = Sort.TOP;

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private RedditClient redditClient;

    @Test
    void testGetPosts() {
        webClient.get()
                .uri(GET_POSTS_PATH, TEST_USERNAME)
                .exchange()
                .expectStatus().isOk();
        verify(redditClient).getPosts(TEST_USERNAME, RedditClient.DEFAULT_LIMIT);

        webClient.get()
                .uri(builder -> builder.path(GET_POSTS_PATH)
                        .queryParam("after", TEST_POST_ID)
                        .build(TEST_USERNAME))
                .exchange()
                .expectStatus().isOk();
        verify(redditClient).getPostsAfter(TEST_USERNAME, TEST_POST_ID, RedditClient.DEFAULT_LIMIT);

        webClient.get()
                .uri(builder -> builder.path(GET_POSTS_PATH)
                        .queryParam("before", TEST_POST_ID)
                        .build(TEST_USERNAME))
                .exchange()
                .expectStatus().isOk();
        verify(redditClient).getPostsBefore(TEST_USERNAME, TEST_POST_ID, RedditClient.DEFAULT_LIMIT);
    }

    @Test
    void testGetPostsWithSorting() {
        webClient.get()
                .uri(GET_POSTS_WITH_SORTING_PATH, TEST_USERNAME, TEST_SORT.name().toLowerCase())
                .exchange()
                .expectStatus().isOk();
        verify(redditClient).getPosts(TEST_USERNAME, TEST_SORT, RedditClient.DEFAULT_LIMIT);


        webClient.get()
                .uri(builder -> builder.path(GET_POSTS_WITH_SORTING_PATH)
                        .queryParam("after", TEST_POST_ID)
                        .build(TEST_USERNAME, TEST_SORT.name().toLowerCase()))
                .exchange()
                .expectStatus().isOk();
        verify(redditClient).getPostsAfter(TEST_USERNAME, TEST_POST_ID, TEST_SORT, RedditClient.DEFAULT_LIMIT);

        webClient.get()
                .uri(builder -> builder.path(GET_POSTS_WITH_SORTING_PATH)
                        .queryParam("before", TEST_POST_ID)
                        .build(TEST_USERNAME, TEST_SORT.name().toLowerCase()))
                .exchange()
                .expectStatus().isOk();
        verify(redditClient).getPostsBefore(TEST_USERNAME, TEST_POST_ID, TEST_SORT, RedditClient.DEFAULT_LIMIT);
    }

}