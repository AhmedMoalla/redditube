package com.amoalla.redditube.client.model;

import com.amoalla.redditube.client.model.deserializer.MediaPostDtoDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonDeserialize(using = MediaPostDtoDeserializer.class)
public class MediaPostDto {
    private final String id;
    private final String mediaUrl;
    private final String mediaThumbnailUrl;
    private final String username;
    private final String subreddit;
    private final String title;
    private final boolean isEmbed;
    private final String embedHtml;
    private final LocalDateTime creationDateTime;
}
