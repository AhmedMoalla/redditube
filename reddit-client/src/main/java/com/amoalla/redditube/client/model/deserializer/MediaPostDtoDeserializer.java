package com.amoalla.redditube.client.model.deserializer;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.client.impl.exception.MediaPostParsingException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class MediaPostDtoDeserializer extends JsonDeserializer<MediaPostDto> {

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
    static final String IS_GALLERY_KEY = "is_gallery";
    static final String MEDIA_METADATA_KEY = "media_metadata";
    static final String PROVIDER_NAME_KEY = "provider_name";

    @Override
    public MediaPostDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        return parseSingleMediaPost(node);
    }

    private MediaPostDto parseSingleMediaPost(JsonNode json) {
        try {
            return MediaPostDto.builder()
                    .id(getText(json, NAME_KEY))
                    .mediaUrl(getMediaUrl(json))
                    .mediaThumbnailUrl(getThumbnailUrl(json))
                    .username(getText(json, AUTHOR_KEY))
                    .subreddit(getText(json, SUBREDDIT_KEY))
                    .title(getText(json, TITLE_KEY))
                    .isEmbed(isEmbed(json))
                    .embedHtml(getEmbedHtml(json))
                    .embedProviderName(getEmbedProviderName(json))
                    .creationDateTime(getDateTime(json))
                    .isGallery(isGallery(json))
                    .galleryMediaUrls(getGalleryMediaUrls(json))
                    .build();
        } catch (Exception e) {
            throw new MediaPostParsingException(json, e);
        }
    }

    private String getEmbedProviderName(JsonNode json) {
        if (isEmbed(json)) {
            return json.get(MEDIA_KEY).get(EMBED_KEY).get(PROVIDER_NAME_KEY).asText();
        }
        return null;
    }

    private boolean isGallery(JsonNode json) {
        return json.has(IS_GALLERY_KEY) && json.get(IS_GALLERY_KEY).asBoolean();
    }

    private Map<String, String> getGalleryMediaUrls(JsonNode json) {
        Map<String, String> galleryMediaUrls = new HashMap<>();
        JsonNode mediaMetadata = json.get(MEDIA_METADATA_KEY);
        if (isGallery(json) && json.has(MEDIA_METADATA_KEY) && mediaMetadata.size() > 0) {
            for (JsonNode media : mediaMetadata) {
                String mediaId = media.get("id").asText();
                String mediaUrl = StringEscapeUtils.unescapeHtml4(media.get("s").get("u").asText());
                galleryMediaUrls.put(mediaId, mediaUrl);
            }
        }
        return galleryMediaUrls;
    }

    private String getMediaUrl(JsonNode json) {
        return StringEscapeUtils.unescapeHtml4(getText(json, MEDIA_URL_KEY));
    }

    private String getThumbnailUrl(JsonNode json) {
        String thumbnailUrl = getText(json, THUMBNAIL_KEY);
        return thumbnailUrl == null || thumbnailUrl.equals("default") ? getMediaUrl(json) : thumbnailUrl;
    }

    private String getText(JsonNode node, String key) {
        JsonNode value = node.get(key);
        return value != null ? value.asText() : null;
    }

    private LocalDateTime getDateTime(JsonNode json) {
        long createdUtcSeconds = json.get(CREATED_UTC).asLong();
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(createdUtcSeconds), ZoneId.of("UTC"));
    }

    private boolean isEmbed(JsonNode json) {
        return json.has(MEDIA_KEY)
                && json.get(MEDIA_KEY).has(EMBED_KEY);
    }

    private String getEmbedHtml(JsonNode json) {
        return isEmbed(json)
                ? StringEscapeUtils.unescapeHtml4(json.get(MEDIA_KEY)
                .get(EMBED_KEY)
                .get(HTML_KEY)
                .asText())
                : null;
    }
}
