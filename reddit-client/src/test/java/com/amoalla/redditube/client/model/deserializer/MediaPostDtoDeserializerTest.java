package com.amoalla.redditube.client.model.deserializer;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.client.configuration.RedditClientConfiguration;
import com.amoalla.redditube.client.model.MediaPostListings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.amoalla.redditube.client.model.deserializer.MediaPostDtoDeserializer.*;
import static org.junit.jupiter.api.Assertions.*;

class MediaPostDtoDeserializerTest {

    private static final String TEST_NAME = "TEST_NAME";
    private static final String TEST_MEDIA_URL = "TEST_MEDIA_URL";
    private static final String TEST_THUMBNAIL = "TEST_THUMBNAIL";
    private static final String TEST_AUTHOR = "TEST_AUTHOR";
    private static final String TEST_SUBREDDIT = "TEST_SUBREDDIT";
    private static final String TEST_TITLE = "TEST_TITLE";
    private static final String TEST_HTML = "TEST_HTML";
    private static final String TEST_MEDIA = String.format("{\"%s\":  {\"%s\":  \"%s\"}}", EMBED_KEY, HTML_KEY, TEST_HTML);
    private static final long TEST_CREATED_UTC = Instant.now().toEpochMilli();
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUpObjectMapper() {
        RedditClientConfiguration config = new RedditClientConfiguration();
        objectMapper = config.configureObjectMapper(new ObjectMapper());
    }

    @Test
    void testDeserializationWorks() throws JsonProcessingException {
        String json = createTestJson();
        MediaPostListings listings = objectMapper.readValue(json, MediaPostListings.class);
        assertNotNull(listings);
        assertEquals(1, listings.getMediaPosts().size());
        MediaPostDto mediaPostDto = listings.getMediaPosts().get(0);

        assertEquals(TEST_NAME, mediaPostDto.getId());
        assertEquals(TEST_MEDIA_URL, mediaPostDto.getMediaUrl());
        assertEquals(TEST_THUMBNAIL, mediaPostDto.getMediaThumbnailUrl());
        assertEquals(TEST_AUTHOR, mediaPostDto.getUsername());
        assertEquals(TEST_SUBREDDIT, mediaPostDto.getSubreddit());
        assertEquals(TEST_TITLE, mediaPostDto.getTitle());
        assertTrue(mediaPostDto.isEmbed());
        assertEquals(TEST_HTML, mediaPostDto.getEmbedHtml());
        LocalDateTime expectedCreationDate = LocalDateTime.ofInstant(Instant.ofEpochMilli(TEST_CREATED_UTC), ZoneId.systemDefault());
        assertEquals(expectedCreationDate, mediaPostDto.getCreationDateTime());
    }

    private String createTestJson() {
        return String.format("{\"data\":  { \"children\": [{\"data\": {\"%s\":  \"%s\", \"%s\": \"%s\", \"%s\": \"%s\", \"%s\": \"%s\", \"%s\": \"%s\", \"%s\": \"%s\", \"%s\": %s, \"%s\": %s}}] }}",
                NAME_KEY, TEST_NAME,
                MEDIA_URL_KEY, TEST_MEDIA_URL,
                THUMBNAIL_KEY, TEST_THUMBNAIL,
                AUTHOR_KEY, TEST_AUTHOR,
                SUBREDDIT_KEY, TEST_SUBREDDIT,
                TITLE_KEY, TEST_TITLE,
                MEDIA_KEY, TEST_MEDIA,
                CREATED_UTC, TEST_CREATED_UTC);
    }
}