package com.amoalla.redditube.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MediaPostDto {
    private String id;
    private String mediaUrl;
    private String mediaThumbnailUrl;
    private String username;
    private String subreddit;
    private String title;
    private boolean isEmbed;
    private String embedHtml;
    private String embedProviderName;
    private LocalDateTime creationDateTime;
    private boolean isGallery;
    private Map<String, String> galleryMediaUrls;
}
