package com.amoalla.redditube.commons.security;

import com.amoalla.redditube.commons.RedditConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@Profile("!"+ RedditConstants.SECURITY_ENABLED_PROFILE)
public class LocalhostSecurityConfiguration {

    @Bean
    SecurityWebFilterChain disableSecurityConfiguration(ServerHttpSecurity http) {
        http.authorizeExchange().anyExchange().permitAll();
        http.csrf().disable();
        return http.build();
    }
}
