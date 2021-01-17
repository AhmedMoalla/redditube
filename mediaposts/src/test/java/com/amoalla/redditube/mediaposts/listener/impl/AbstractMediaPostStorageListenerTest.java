package com.amoalla.redditube.mediaposts.listener.impl;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.mediaposts.entity.MediaPost;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.SubscribableType;
import com.amoalla.redditube.mediaposts.listener.NewSingleMediaPostAvailableEvent;
import com.amoalla.redditube.mediaposts.repository.MediaPostRepository;
import com.amoalla.redditube.mediaposts.storage.StorageService;
import com.amoalla.redditube.mediaposts.storage.cache.MediaHashCache;
import com.amoalla.redditube.mediaposts.storage.exception.StorageException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.concurrent.CompletableToListenableFutureAdapter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AbstractMediaPostStorageListenerTest {

    private static final String TEST_OBJECT_ID = "TEST_OBJECT_ID";
    private static final String TEST_BUCKET_NAME = "TEST_BUCKET_NAME";

    private static MockWebServer mockWebServer;

    private ThreadPoolTaskScheduler scheduler;
    private AbstractMediaPostStorageListener listener;
    private MediaPostDto testPost;
    private MediaPostRepository repository;

    @BeforeAll
    static void setUpServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() throws StorageException {
        testPost = new MediaPostDto();
        testPost.setId("ID");
        testPost.setTitle("TITLE");
        testPost.setCreationDateTime(LocalDateTime.now());
        testPost.setMediaUrl(String.format("http://localhost:%d/media.jpg?test=test", mockWebServer.getPort()));
        testPost.setMediaThumbnailUrl(String.format("http://localhost:%d/thumbnail.jpg", mockWebServer.getPort()));
        repository = mock(MediaPostRepository.class);
        when(repository.save(any())).thenReturn(new MediaPost());
        StorageService storageService = mock(StorageService.class);
        when(storageService.uploadMediaToStorage(any(), any(), any()))
            .thenReturn(TEST_OBJECT_ID);
        scheduler = mock(ThreadPoolTaskScheduler.class);
        when(scheduler.submitListenable(any(Callable.class)))
                .thenReturn(new CompletableToListenableFutureAdapter(CompletableFuture.completedFuture(null)));
        MediaHashCache mediaHashCache = mock(MediaHashCache.class);
        listener = new AbstractMediaPostStorageListener(repository, storageService, scheduler, mediaHashCache) {
            @Override
            public void onNewMediaPostAvailable(NewSingleMediaPostAvailableEvent event) {
                // Nothing to do
            }

            @Override
            public boolean matches(MediaPostDto mediaPostDto) {
                return true;
            }
        };
    }

    @Test
    void testUploadAndSaveMediaPost() throws Exception {
        mockWebServer.enqueue(new MockResponse());
        mockWebServer.enqueue(new MockResponse());
        ArgumentCaptor<Callable<Void>> captor = ArgumentCaptor.forClass(Callable.class);
        listener.uploadAndSaveMediaPost(testPost, new Subscribable(), TEST_BUCKET_NAME, () -> {});
        verify(scheduler, times(2)).submitListenable(captor.capture());
        List<Callable<Void>> allValues = captor.getAllValues();
        for (Callable<Void> uploadTask : allValues) {
            uploadTask.call();
        }
        verify(listener.storageService).uploadMediaToStorage(any(), eq("media.jpg"), eq(TEST_BUCKET_NAME));
        verify(listener.storageService).uploadMediaToStorage(any(), eq("thumbnail.jpg"), eq(TEST_BUCKET_NAME));
    }

    @Test
    void testMapToEntity() {
        Subscribable subscribable = new Subscribable();
        MediaPost mediaPost = listener.mapToEntity(testPost, subscribable);
        ModelMapper modelMapper = new ModelMapper();
        MediaPost expectedMediaPost = modelMapper.map(testPost, MediaPost.class);
        expectedMediaPost.setOwner(subscribable);
        assertEquals(expectedMediaPost, mediaPost);
    }

    @Test
    void testGetBucketNameForSubscribable() {
        Subscribable subscribable = new Subscribable();
        subscribable.setType(SubscribableType.USER);
        subscribable.setHandle("handle");
        String bucketName = listener.getBucketNameForSubscribable(subscribable);
        assertEquals("u.handle", bucketName);

        subscribable.setType(SubscribableType.SUBREDDIT);
        bucketName = listener.getBucketNameForSubscribable(subscribable);
        assertEquals("r.handle", bucketName);
    }
}