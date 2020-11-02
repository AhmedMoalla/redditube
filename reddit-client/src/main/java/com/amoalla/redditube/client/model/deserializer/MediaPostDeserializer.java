package com.amoalla.redditube.client.model.deserializer;

import com.amoalla.redditube.client.impl.exception.MediaPostParsingException;
import com.amoalla.redditube.client.model.MediaPost;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class MediaPostDeserializer extends JsonDeserializer<MediaPost> {

    private static final String NAME_KEY = "name";
    private static final String MEDIA_URL_KEY = "url_overridden_by_dest";
    private static final String THUMBNAIL_KEY = "thumbnail";
    private static final String AUTHOR_KEY = "author";
    private static final String SUBREDDIT_KEY = "subreddit";
    private static final String TITLE_KEY = "title";
    private static final String MEDIA_KEY = "media";
    private static final String EMBED_KEY = "oembed";
    private static final String HTML_KEY = "html";
    private static final String CREATED_UTC = "created_utc";

    @Override
    public MediaPost deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        return parseSingleMediaPost(node);
    }

    private MediaPost parseSingleMediaPost(JsonNode json) {
        try {
            return MediaPost.builder()
                    .id(getText(json, NAME_KEY))
                    .mediaUrl(getText(json, MEDIA_URL_KEY))
                    .mediaThumbnailUrl(getText(json, THUMBNAIL_KEY))
                    .username(getText(json, AUTHOR_KEY))
                    .subreddit(getText(json, SUBREDDIT_KEY))
                    .title(getText(json, TITLE_KEY))
                    .isEmbed(isEmbed(json))
                    .embedHtml(getEmbedHtml(json))
                    .creationDateTime(getDateTime(json))
                    .build();
        } catch (Exception e) {
            throw new MediaPostParsingException(json, e);
        }
    }

    private String getText(JsonNode node, String key) {
        JsonNode value = node.get(key);
        return value != null ? value.asText() : null;
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
}
