package com.amoalla.redditube.gateway.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("redditube.idp")
public class IdpProperties {
    private String host;
    private int port;
    private String realm;
    private String clientId;
    private String clientSecret;
}
