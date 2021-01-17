package com.amoalla.redditube.mediaposts.listener.impl;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.listener.NewSingleMediaPostAvailableEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GalleryMediaPostStorageListenerTest {
    private GalleryMediaPostStorageListener listener;

    @BeforeEach
    void setUp() {
        listener = Mockito.mock(GalleryMediaPostStorageListener.class);
        doAnswer(InvocationOnMock::callRealMethod).when(listener).onNewMediaPostAvailable(Mockito.any());
        when(listener.matches(Mockito.any())).thenCallRealMethod();
    }

    @Test
    void testOnMediaPostAvailable() {
        Subscribable subscribable = new Subscribable();
        MediaPostDto dto = new MediaPostDto();
        dto.setId("ID");
        dto.setGalleryMediaUrls(Map.of("ID1", "URL1", "ID2", "URL2"));
        String bucketName = "BUCKET_NAME";
        Runnable runnable = mock(Runnable.class);
        var event = new NewSingleMediaPostAvailableEvent(subscribable, dto, bucketName, runnable);
        listener.onNewMediaPostAvailable(event);

        MediaPostDto expectedDto = dto.toBuilder().build();
        expectedDto.setId("ID1");
        expectedDto.setMediaUrl("URL1");
        expectedDto.setMediaThumbnailUrl("URL1");
        verify(listener).uploadAndSaveMediaPost(expectedDto, subscribable, bucketName, runnable);

        expectedDto = dto.toBuilder().build();
        expectedDto.setId("ID2");
        expectedDto.setMediaUrl("URL2");
        expectedDto.setMediaThumbnailUrl("URL2");
        verify(listener).uploadAndSaveMediaPost(expectedDto, subscribable, bucketName, runnable);
    }

    @Test
    void testMatches() {
        MediaPostDto dto = new MediaPostDto();
        dto.setGallery(true);
        assertTrue(listener.matches(dto));
    }
}