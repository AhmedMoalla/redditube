package com.amoalla.redditube.mediaposts.listener.impl;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.mediaposts.entity.MediaPost;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.listener.NewSingleMediaPostAvailableEvent;
import com.amoalla.redditube.mediaposts.listener.resolver.EmbedUrlResolver;
import com.amoalla.redditube.mediaposts.listener.resolver.ResolverMediaUrls;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmbedMediaPostStorageListenerTest {

    private static final String PROVIDER_NAME = "PROVIDER_NAME";
    private final String EMBED_HTML = "EMBED_HTML";
    private final String MEDIA_URL = "MEDIA_URL";
    private final String MEDIA_THUMBNAIL_URL = "MEDIA_THUMBNAIL_URL";

    private EmbedMediaPostStorageListener listener;

    @BeforeEach
    void setUp() {
        EmbedUrlResolver resolver = mock(EmbedUrlResolver.class);
        when(resolver.resolve(EMBED_HTML))
                .thenReturn(new ResolverMediaUrls(MEDIA_URL, MEDIA_THUMBNAIL_URL));
        listener = mock(EmbedMediaPostStorageListener.class);
        ReflectionTestUtils.setField(listener, "urlResolvers", Map.of(PROVIDER_NAME, resolver));
        doAnswer(InvocationOnMock::callRealMethod).when(listener).onNewMediaPostAvailable(Mockito.any());
        when(listener.mapToEntity(Mockito.any(), Mockito.any())).thenCallRealMethod();
        when(listener.matches(Mockito.any())).thenCallRealMethod();
    }

    @Test
    void testOnNewMediaPostAvailable() {
        Subscribable subscribable = new Subscribable();
        MediaPostDto dto = new MediaPostDto();
        dto.setEmbedHtml(EMBED_HTML);
        dto.setEmbedProviderName(PROVIDER_NAME);
        String bucketName = "TEST_BUCKET";

        Runnable runnable = mock(Runnable.class);
        listener.onNewMediaPostAvailable(new NewSingleMediaPostAvailableEvent(subscribable, dto, bucketName, runnable));

        assertEquals(MEDIA_URL, dto.getMediaUrl());
        assertEquals(MEDIA_THUMBNAIL_URL, dto.getMediaThumbnailUrl());
        verify(listener).uploadAndSaveMediaPost(dto, subscribable, bucketName, runnable);
    }

    @Test
    void testMapToEntity() {
        MediaPostDto dto = new MediaPostDto();
        dto.setId("ID");
        dto.setTitle("TITLE");
        dto.setCreationDateTime(LocalDateTime.now());
        dto.setEmbed(true);
        Subscribable subscribable = new Subscribable();

        MediaPost mediaPost = listener.mapToEntity(dto, subscribable);

        ModelMapper modelMapper = new ModelMapper();
        MediaPost expectedMediaPost = modelMapper.map(dto, MediaPost.class);
        expectedMediaPost.setIsVideo(true);
        expectedMediaPost.setOwner(subscribable);

        assertEquals(expectedMediaPost, mediaPost);
    }

    @Test
    void testMatches() {
        MediaPostDto dto = new MediaPostDto();
        dto.setEmbed(true);
        assertTrue(listener.matches(dto));

        dto.setEmbed(false);
        assertFalse(listener.matches(dto));
    }
}