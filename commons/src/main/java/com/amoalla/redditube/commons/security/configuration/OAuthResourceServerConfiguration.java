package com.amoalla.redditube.commons.security.configuration;

import com.amoalla.redditube.commons.security.converter.UserIdPrincipalJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@Profile("oauth")
@EnableWebFluxSecurity
public class OAuthResourceServerConfiguration {

    @Bean
    public SecurityWebFilterChain configureSecurity(ServerHttpSecurity http) {
        http.authorizeExchange().anyExchange().authenticated();
        http.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(new UserIdPrincipalJwtAuthenticationConverter());
        http.csrf().disable();
        return http.build();
    }

}