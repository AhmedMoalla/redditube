package com.amoalla.redditube.mediaposts.scraper;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.api.service.ExplorerServices;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.SubscribableType;
import com.amoalla.redditube.mediaposts.repository.SubscribableRepository;
import com.amoalla.redditube.mediaposts.scraper.FetchMediaPostsSubTask.FetchMediaPostsResult;
import com.amoalla.redditube.mediaposts.scraper.configuration.ScraperSchedulerProperties;
import com.amoalla.redditube.mediaposts.scraper.event.MediaPostsScraperTaskFinished;
import com.amoalla.redditube.mediaposts.scraper.event.NewMediaPostsAvailableEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MediaPostsScraperTask.class, MediaPostsScraperTaskTest.TestEventListener.class})
class MediaPostsScraperTaskTest {

    private static final String TEST_HANDLE_USER = "TEST_HANDLE_USER";
    private static final String TEST_POST_ID = "TEST_POST_ID";

    @MockBean
    private ApplicationEventPublisher eventPublisher;
    @MockBean
    private ThreadPoolTaskScheduler scheduler;
    @MockBean
    private SubscribableRepository repository;
    @MockBean
    private ExplorerServices explorerServices;
    @MockBean
    private ScraperSchedulerProperties properties;
    @MockBean
    private ExplorerService explorerService;

    @Autowired
    private MediaPostsScraperTask task;

    @BeforeEach
    void setUp() {
        Subscribable subscribable = new Subscribable();
        subscribable.setHandle(TEST_HANDLE_USER);
        subscribable.setType(SubscribableType.USER);
        subscribable.setLastFetchedPostCount(50);
        when(repository.findAll()).thenReturn(Arrays.asList(subscribable));

        when(explorerService.getPosts(any(), any(), any(), anyInt()))
                .thenReturn(Flux.fromIterable(Collections.nCopies(100, new MediaPostDto())));

        when(explorerServices.users()).thenReturn(explorerService);
        when(explorerServices.subreddits()).thenReturn(explorerService);
        when(properties.getRestartPeriod()).thenReturn(Duration.ofSeconds(10));
        task.afterPropertiesSet();
    }

    @Test
    void testRunTask() {
        verify(scheduler).execute(task);

        ListenableFuture<FetchMediaPostsResult> future = mock(ListenableFuture.class);

        AtomicInteger taskCounter = new AtomicInteger(0);
        doAnswer(invocation -> {
            int counterValue = taskCounter.incrementAndGet();
            SuccessCallback<FetchMediaPostsResult> successCallback = invocation.getArgument(0);

            FetchMediaPostsSubTask originalTask = new FetchMediaPostsSubTask(explorerService, new Subscribable());

            FetchMediaPostsResult result = null;
            if (counterValue == 1) {
                result = new FetchMediaPostsResult(true, TEST_POST_ID,
                        Collections.nCopies(100, new MediaPostDto()), originalTask);

            }

            if (counterValue == 2) {
                result = new FetchMediaPostsResult(false, TEST_POST_ID,
                        Collections.nCopies(20, new MediaPostDto()), originalTask);
            }

            successCallback.onSuccess(result);
            return null;
        }).when(future).addCallback(any(), any());
        when(scheduler.submitListenable(any(Callable.class))).thenReturn(future);

        task.run();
    }

    @Test
    void testOnMediaPostsScraperTaskFinished() {
        task.onMediaPostsScraperTaskFinished(new MediaPostsScraperTaskFinished(1));
        verify(scheduler).schedule(Mockito.eq(task), any(Instant.class));
    }

    public static class TestEventListener {
        private final AtomicInteger eventCounter = new AtomicInteger(0);

        @EventListener
        public void onNewMediaPostsAvailableEvent(NewMediaPostsAvailableEvent event) {
            int counter = eventCounter.incrementAndGet();
            assertEquals(TEST_HANDLE_USER, event.getSubscribable().getHandle());
            assertEquals(SubscribableType.USER, event.getSubscribable().getType());

            if (counter == 1) {
                assertEquals(100, event.getMediaPosts().size());
            }

            if (counter == 2) {
                assertEquals(20, event.getMediaPosts().size());
            }
        }
    }
}