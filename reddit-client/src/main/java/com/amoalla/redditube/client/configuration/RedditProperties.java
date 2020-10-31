package com.amoalla.redditube.client.configuration;

import com.amoalla.redditube.client.impl.MediaPostRequester;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("reddit")
public class RedditProperties {
    private String username;
    private String password;
    private String clientId;
    private String clientSecret;
    private MediaPostRequester.Type type;
}
