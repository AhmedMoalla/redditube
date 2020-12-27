package com.amoalla.redditube.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@Profile("oauth")
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
