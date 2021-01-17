package com.amoalla.redditube.mediaposts.listener.resolver.impl;

import com.amoalla.redditube.mediaposts.listener.resolver.ResolverMediaUrls;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RedGifsEmbedUrlResolverTest {


    private static final String MEDIA_URL = "MEDIA_URL";
    private static final String MEDIA_THUMBNAIL_URL = "MEDIA_THUMBNAIL_URL";
    //language=HTML
    private static final String HTML_RESPONSE = String.format("<html lang='fr'>\n" +
            "<body>\n" +
            "<video poster='%s'>\n" +
            "    <source src=\"%s\" type='video/mp4'/>\n" +
            "    <source src=\"should_not_be_selected-mobile\" type='video/mp4'/>\n" +
            "    <source src=\"should_not_be_selected_wrong_type\" type='video/webp'/>\n" +
            "</video>\n" +
            "</body>\n" +
            "</html>", MEDIA_THUMBNAIL_URL, MEDIA_URL);

    private static MockWebServer mockWebServer;

    private final RedGifsEmbedUrlResolver resolver = new RedGifsEmbedUrlResolver();

    @BeforeAll
    static void setUpMockWebServer() {
        mockWebServer = new MockWebServer();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testResolve() {
        String embedHtml = String.format("<iframe src=\"http://localhost:%d\"/>", mockWebServer.getPort());
        mockWebServer.enqueue(new MockResponse()
                .setBody(HTML_RESPONSE)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE));
        ResolverMediaUrls resolve = resolver.resolve(embedHtml);
        assertEquals(MEDIA_URL, resolve.getMediaUrl());
        assertEquals(MEDIA_THUMBNAIL_URL, resolve.getMediaThumbnailUrl());
    }

    @Test
    void testGetProviderName() {
        assertEquals("RedGIFS", resolver.getProviderName());
    }
}