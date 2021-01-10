package com.amoalla.redditube.explorer.service;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.dto.Sort;
import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.client.RedditClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ExplorerServiceImpl.class)
class ExplorerServiceImplTest {

    private static final int TEST_LIMIT = 20;
    private static final String TEST_AFTER = "TEST_AFTER";
    private static final String TEST_BEFORE = "TEST_BEFORE";
    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final Sort TEST_SORT = Sort.NEW;

    @MockBean
    private RedditClient redditClient;

    @Autowired
    private ExplorerService explorerService;

    @Test
    void testGetPosts() {
        when(redditClient.getPostsAfter(TEST_USERNAME, TEST_AFTER, TEST_LIMIT)).thenReturn(Flux.just());
        Flux<MediaPostDto> posts = explorerService.getPosts(TEST_USERNAME, TEST_AFTER, "", TEST_LIMIT);
        StepVerifier.create(posts)
                .expectComplete()
                .verify();
        verify(redditClient).getPostsAfter(TEST_USERNAME, TEST_AFTER, TEST_LIMIT);

        when(redditClient.getPostsBefore(TEST_USERNAME, TEST_BEFORE, TEST_LIMIT)).thenReturn(Flux.just());
        posts = explorerService.getPosts(TEST_USERNAME, "", TEST_BEFORE, TEST_LIMIT);
        StepVerifier.create(posts)
                .expectComplete()
                .verify();
        verify(redditClient).getPostsBefore(TEST_USERNAME, TEST_BEFORE, TEST_LIMIT);

        when(redditClient.getPosts(TEST_USERNAME, TEST_LIMIT)).thenReturn(Flux.just());
        posts = explorerService.getPosts(TEST_USERNAME, "", "", TEST_LIMIT);
        StepVerifier.create(posts)
                .expectComplete()
                .verify();
        verify(redditClient).getPosts(TEST_USERNAME, TEST_LIMIT);
    }

    @Test
    void testGetPostsWithSorting() {
        when(redditClient.getPostsAfter(TEST_USERNAME, TEST_AFTER, TEST_SORT, TEST_LIMIT)).thenReturn(Flux.just());
        Flux<MediaPostDto> posts = explorerService.getPosts(TEST_USERNAME, TEST_AFTER, "", TEST_LIMIT, TEST_SORT);
        StepVerifier.create(posts)
                .expectComplete()
                .verify();
        verify(redditClient).getPostsAfter(TEST_USERNAME, TEST_AFTER, TEST_SORT, TEST_LIMIT);

        when(redditClient.getPostsBefore(TEST_USERNAME, TEST_BEFORE, TEST_SORT, TEST_LIMIT)).thenReturn(Flux.just());
        posts = explorerService.getPosts(TEST_USERNAME, "", TEST_BEFORE, TEST_LIMIT, TEST_SORT);
        StepVerifier.create(posts)
                .expectComplete()
                .verify();
        verify(redditClient).getPostsBefore(TEST_USERNAME, TEST_BEFORE, TEST_SORT, TEST_LIMIT);

        when(redditClient.getPosts(TEST_USERNAME, TEST_SORT, TEST_LIMIT)).thenReturn(Flux.just());
        posts = explorerService.getPosts(TEST_USERNAME, "", "", TEST_LIMIT, TEST_SORT);
        StepVerifier.create(posts)
                .expectComplete()
                .verify();
        verify(redditClient).getPosts(TEST_USERNAME, TEST_SORT, TEST_LIMIT);
    }
}