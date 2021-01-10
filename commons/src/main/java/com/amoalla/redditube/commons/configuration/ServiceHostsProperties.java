package com.amoalla.redditube.commons.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("redditube.services")
public class ServiceHostsProperties {
    private ServiceHost explorerUsers;
    private ServiceHost explorerSubreddits;
    private ServiceHost mediaPosts;

    @Getter
    @Setter
    public static class ServiceHost {
        private String host;
        private int port;
        private int grpcPort;
    }
}
