package com.amoalla.redditube.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
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
    private LocalDateTime creationDateTime;
}
