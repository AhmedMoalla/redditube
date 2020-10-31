package com.amoalla.redditube.gateway.configuration;

import org.springframework.cloud.security.oauth2.gateway.TokenRelayAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@Profile("oauth")
@Import(TokenRelayAutoConfiguration.class)
public class OAuthLoginServerConfiguration {

    @Bean
    SecurityWebFilterChain configureOAuth2LoginServer(ServerHttpSecurity http) {
        http.authorizeExchange().anyExchange().authenticated();
        http.csrf().disable();
        http.oauth2Login();
        http.oauth2ResourceServer().jwt();
        return http.build();
    }
}
