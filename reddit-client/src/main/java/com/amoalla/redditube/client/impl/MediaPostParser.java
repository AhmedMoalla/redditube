package com.amoalla.redditube.client.impl;

import com.amoalla.redditube.client.MediaPost;
import com.amoalla.redditube.client.impl.exception.MediaPostParsingException;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Parser for "listings" objects received from reddit API.
 * Parses received json objects to MediaPosts
 */
public class MediaPostParser {

    static final String DATA_KEY = "data";
    static final String CHILDREN_KEY = "children";
    static final String NAME_KEY = "name";
    static final String MEDIA_URL_KEY = "url_overridden_by_dest";
    static final String THUMBNAIL_KEY = "thumbnail";
    static final String AUTHOR_KEY = "author";
    static final String SUBREDDIT_KEY = "subreddit";
    static final String TITLE_KEY = "title";
    static final String MEDIA_KEY = "media";
    static final String EMBED_KEY = "oembed";
    static final String HTML_KEY = "html";
    static final String CREATED_UTC = "created_utc";

    public Flux<MediaPost> parse(JsonNode jsonNode) {
        return Flux.just(jsonNode)
                .map(json -> json.get(DATA_KEY))
                .map(json -> json.get(CHILDREN_KEY))
                .flatMapIterable(this::parseMediaPosts);
    }

    private List<MediaPost> parseMediaPosts(JsonNode children) {
        return toStream(children.elements())
                .map(json -> json.get(DATA_KEY))
                .filter(this::containsMedia)
                .map(this::parseSingleMediaPost)
                .collect(Collectors.toList());
    }

    private MediaPost parseSingleMediaPost(JsonNode json) {
        try {
            return MediaPost.builder()
                    .id(json.get(NAME_KEY).asText())
                    .mediaUrl(json.get(MEDIA_URL_KEY).asText())
                    .mediaThumbnailUrl(json.get(THUMBNAIL_KEY).asText())
                    .username(json.get(AUTHOR_KEY).asText())
                    .subreddit(json.get(SUBREDDIT_KEY).asText())
                    .title(json.get(TITLE_KEY).asText())
                    .isEmbed(isEmbed(json))
                    .embedHtml(getEmbedHtml(json))
                    .creationDateTime(getDateTime(json))
                    .build();
        } catch (Exception e) {
            throw new MediaPostParsingException(json, e);
        }
    }

    private LocalDateTime getDateTime(JsonNode json) {
        long millis = json.get(CREATED_UTC).asLong();
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    private boolean isEmbed(JsonNode json) {
        return json.has(MEDIA_KEY)
                && json.get(MEDIA_KEY).has(EMBED_KEY);
    }

    private String getEmbedHtml(JsonNode json) {
        return isEmbed(json)
                ? json.get(MEDIA_KEY)
                .get(EMBED_KEY)
                .get(HTML_KEY)
                .asText()
                : null;
    }

    private <T> Stream<T> toStream(Iterator<T> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator,
                        Spliterator.ORDERED), false);
    }

    private boolean containsMedia(JsonNode json) {
        return json.get(MEDIA_URL_KEY) != null;
    }
}
