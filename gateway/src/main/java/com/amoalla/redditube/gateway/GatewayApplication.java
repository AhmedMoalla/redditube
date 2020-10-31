package com.amoalla.redditube.gateway;

import com.amoalla.redditube.gateway.configuration.properties.DexProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.security.oauth2.gateway.TokenRelayAutoConfiguration;

@SpringBootApplication(exclude = TokenRelayAutoConfiguration.class)
@EnableConfigurationProperties(DexProperties.class)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
