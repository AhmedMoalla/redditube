package com.amoalla.redditube.commons.security;

import com.amoalla.redditube.commons.RedditConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2ResourceServerSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@Profile(RedditConstants.SECURITY_ENABLED_PROFILE)
@EnableWebFluxSecurity
public class OAuthResourceServerConfiguration {

    @Bean
    public SecurityWebFilterChain configureSecurity(ServerHttpSecurity http) {
        http.authorizeExchange().anyExchange().authenticated();
        http.oauth2ResourceServer().jwt();
        http.csrf().disable();
        return http.build();
    }

}