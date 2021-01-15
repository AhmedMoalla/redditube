package com.amoalla.redditube.mediaposts.listener.impl;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.SubscribableType;
import com.amoalla.redditube.mediaposts.listener.MediaPostStorageListener;
import com.amoalla.redditube.mediaposts.listener.NewSingleMediaPostAvailableEvent;
import com.amoalla.redditube.mediaposts.scraper.event.NewMediaPostsAvailableEvent;
import com.amoalla.redditube.mediaposts.storage.StorageService;
import com.amoalla.redditube.mediaposts.storage.exception.StorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CompositeMediaPostStorageListenerTest {

    private static final String TEST_BUCKET_NAME = "u.handle";

    private CompositeMediaPostStorageListener listener;
    private StorageService storageService;
    private MediaPostStorageListener storageListener;

    @BeforeEach
    void setUp() throws StorageException {
        listener = mock(CompositeMediaPostStorageListener.class);
        doAnswer(InvocationOnMock::callRealMethod).when(listener).onNewMediaPostsAvailableEvent(Mockito.any());
        doAnswer(InvocationOnMock::callRealMethod).when(listener).onNewMediaPostAvailable(Mockito.any());
        when(listener.matches(Mockito.any())).thenCallRealMethod();
        when(listener.getBucketNameForSubscribable(Mockito.any())).thenCallRealMethod();

        storageService = mock(StorageService.class);
        when(storageService.createBucketIfNotExists(Mockito.any())).thenReturn(TEST_BUCKET_NAME);
        ReflectionTestUtils.setField(listener, "storageService", storageService);

        storageListener = mock(MediaPostStorageListener.class);
        when(storageListener.matches(Mockito.any())).thenReturn(true);
        doAnswer(invocation -> null).when(storageListener).onNewMediaPostAvailable(Mockito.any());
        ReflectionTestUtils.setField(listener, "listeners", Collections.singletonList(storageListener));
    }

    @Test
    void testOnNewMediaPostsAvailableEvent() throws StorageException {
        Subscribable subscribable = new Subscribable();
        subscribable.setHandle("handle");
        subscribable.setType(SubscribableType.USER);
        MediaPostDto dto = new MediaPostDto();
        var event = new NewMediaPostsAvailableEvent(subscribable, Collections.singletonList(dto));
        listener.onNewMediaPostsAvailableEvent(event);

        verify(storageService).createBucketIfNotExists(TEST_BUCKET_NAME);
        var expectedEvent = new NewSingleMediaPostAvailableEvent(subscribable, dto, TEST_BUCKET_NAME);
        ArgumentCaptor<NewSingleMediaPostAvailableEvent> captor = ArgumentCaptor.forClass(NewSingleMediaPostAvailableEvent.class);
        verify(storageListener).onNewMediaPostAvailable(captor.capture());
        NewSingleMediaPostAvailableEvent receivedValue = captor.getValue();
        assertEquals(expectedEvent.getMediaPost(), receivedValue.getMediaPost());
        assertEquals(expectedEvent.getSubscribable(), receivedValue.getSubscribable());
        assertEquals(expectedEvent.getBucketName(), receivedValue.getBucketName());
    }

    @Test
    void testOnNewMediaPostAvailable() {
        Subscribable subscribable = new Subscribable();
        MediaPostDto dto = new MediaPostDto();
        String bucketName = "bucketName";
        var event = new NewSingleMediaPostAvailableEvent(subscribable, dto, bucketName);
        listener.onNewMediaPostAvailable(event);
        verify(listener).uploadAndSaveMediaPost(dto, subscribable, bucketName);
    }

    @Test
    void testMatches() {
        assertTrue(listener.matches(null));
    }
}