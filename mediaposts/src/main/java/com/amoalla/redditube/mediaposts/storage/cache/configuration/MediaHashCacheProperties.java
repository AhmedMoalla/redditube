package com.amoalla.redditube.mediaposts.storage.cache.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("redditube.media-hash-cache")
public class MediaHashCacheProperties {
    private boolean enabled;
    private boolean persistent;
}
