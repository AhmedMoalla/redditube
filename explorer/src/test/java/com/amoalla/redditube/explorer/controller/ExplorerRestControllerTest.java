package com.amoalla.redditube.explorer.controller;

import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.client.RedditClient;
import com.amoalla.redditube.api.dto.Sort;
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
@ContextConfiguration(classes = {CommonsConfiguration.class, ExplorerRestController.class})
class ExplorerRestControllerTest {

    private static final String GET_POSTS_PATH = "/{usernameOrSubreddit}";
    private static final String GET_POSTS_WITH_SORTING_PATH = GET_POSTS_PATH + "/{sort}";
    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_POST_ID = "TEST_POST_ID";
    private static final Sort TEST_SORT = Sort.TOP;

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ExplorerService explorerService;

    @Test
    void testGetPosts() {
        webClient.get()
                .uri(builder -> builder.path(GET_POSTS_PATH)
                        .queryParam("after", TEST_POST_ID)
                        .queryParam("before", TEST_POST_ID)
                        .build(TEST_USERNAME))
                .exchange()
                .expectStatus().isOk();
        verify(explorerService).getPosts(TEST_USERNAME, TEST_POST_ID, TEST_POST_ID, RedditClient.DEFAULT_LIMIT);
    }

    @Test
    void testGetPostsWithSorting() {
        webClient.get()
                .uri(builder -> builder.path(GET_POSTS_WITH_SORTING_PATH)
                        .queryParam("after", TEST_POST_ID)
                        .queryParam("before", TEST_POST_ID)
                        .build(TEST_USERNAME, TEST_SORT.name().toLowerCase()))
                .exchange()
                .expectStatus().isOk();
        verify(explorerService).getPosts(TEST_USERNAME, TEST_POST_ID, TEST_POST_ID, RedditClient.DEFAULT_LIMIT, TEST_SORT);
    }

}