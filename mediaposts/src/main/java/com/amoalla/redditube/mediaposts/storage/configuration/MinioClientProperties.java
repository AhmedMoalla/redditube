package com.amoalla.redditube.mediaposts.storage.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("redditube.minio")
public class MinioClientProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
}
