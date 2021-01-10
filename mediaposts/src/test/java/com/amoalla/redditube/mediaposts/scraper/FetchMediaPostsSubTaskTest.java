package com.amoalla.redditube.mediaposts.scraper;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FetchMediaPostsSubTaskTest {

    private static final String TEST_HANDLE = "TEST_HANDLE";
    private static final String TEST_POST_ID = "TEST_POST_ID";

    private ExplorerService explorerService;
    private Subscribable subscribable;
    private FetchMediaPostsSubTask task;

    @BeforeEach
    void setUp() {
        explorerService = mock(ExplorerService.class);
        subscribable = new Subscribable();
        subscribable.setHandle(TEST_HANDLE);
        subscribable.setLastFetchedPostId(TEST_POST_ID);
        task = new FetchMediaPostsSubTask(explorerService, subscribable);
    }

    @Test
    void testCallTask() {
        MediaPostDto dto = new MediaPostDto();
        dto.setId(TEST_POST_ID);
        when(explorerService.getPosts(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt()))
                .thenReturn(Flux.just(dto));

        FetchMediaPostsSubTask.FetchMediaPostsResult result = task.call();
        assertFalse(result.hasMore());
        assertEquals(TEST_POST_ID, result.getNextAfter());
        assertEquals(1, result.getMediaPosts().size());
        assertEquals(result.getSubTask(), task);
    }

    @Test
    void testCallTaskWhenHasMore() {
        MediaPostDto dto = new MediaPostDto();
        dto.setId(TEST_POST_ID);
        List<MediaPostDto> dtos = Collections.nCopies(100, dto);
        when(explorerService.getPosts(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt()))
                .thenReturn(Flux.fromIterable(dtos));

        FetchMediaPostsSubTask.FetchMediaPostsResult result = task.call();
        assertTrue(result.hasMore());
        assertEquals(TEST_POST_ID, result.getNextAfter());
        assertEquals(100, result.getMediaPosts().size());
        assertEquals(result.getSubTask(), task);
    }

    @Test
    void testCallTaskWhenNoMediaPostsAvailable() {
        when(explorerService.getPosts(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt()))
                .thenReturn(Flux.just());

        FetchMediaPostsSubTask.FetchMediaPostsResult result = task.call();
        assertFalse(result.hasMore());
        assertEquals("", result.getNextAfter());
        assertEquals(0, result.getMediaPosts().size());
        assertEquals(result.getSubTask(), task);
    }

    @Test
    void testCreateTaskFromOtherSubTask() {
        MediaPostDto dto = new MediaPostDto();
        dto.setId(TEST_POST_ID);
        List<MediaPostDto> dtos = Collections.nCopies(100, dto);
        when(explorerService.getPosts(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt()))
                .thenReturn(Flux.fromIterable(dtos));
        FetchMediaPostsSubTask newTask = new FetchMediaPostsSubTask(task, subscribable);
        newTask.call();
        verify(explorerService).getPosts(TEST_HANDLE, TEST_POST_ID, "", 100);
    }
}