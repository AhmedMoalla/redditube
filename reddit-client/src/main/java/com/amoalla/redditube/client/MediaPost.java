package com.amoalla.redditube.client;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MediaPost {
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
