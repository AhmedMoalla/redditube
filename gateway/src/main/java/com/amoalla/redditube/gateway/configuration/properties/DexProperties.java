package com.amoalla.redditube.gateway.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("dex")
public class DexProperties {
    private String host;
    private int port;
}
