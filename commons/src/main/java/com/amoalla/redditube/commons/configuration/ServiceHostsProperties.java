package com.amoalla.redditube.commons.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("redditube.hosts")
public class ServiceHostsProperties {
    private String explorer;
    private String mediaPosts;
}
