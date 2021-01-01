package com.amoalla.redditube.commons.security.configuration;

import com.amoalla.redditube.commons.security.converter.UsernamePrincipalJwtAuthenticationConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

import javax.annotation.security.PermitAll;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

/**
 * Adds OAuth 2 resource server configuration.
 * Registers {@link UsernamePrincipalJwtAuthenticationConverter} to obtain the correct {@link AuthenticationPrincipal} in services.
 * Add the ability to use {@link PermitAll} to make specific controller methods unsecure
 */
@Configuration
@Profile("oauth")
@EnableWebFluxSecurity
public class OAuthResourceServerConfiguration {

    @Bean
    public SecurityWebFilterChain configureSecurity(ServerHttpSecurity http,
                                                    @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping mappings) {

        var unsecureMatchers = createMatchers(mappings);
        if (unsecureMatchers != null && !unsecureMatchers.isEmpty()) {
            http.authorizeExchange().matchers(unsecureMatchers.toArray(new ServerWebExchangeMatcher[0])).permitAll();
        }
        http.authorizeExchange().anyExchange().authenticated();
        http.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(new UsernamePrincipalJwtAuthenticationConverter());
        http.csrf().disable();
        return http.build();
    }

    private List<ServerWebExchangeMatcher> createMatchers(RequestMappingHandlerMapping mappings) {
        return mappings.getHandlerMethods()
                .entrySet()
                .stream()
                .filter(entry -> hasAnnotation(entry.getValue(), PermitAll.class))
                .flatMap(entry -> createMatchersFromRequestMappingInfo(entry.getKey()).stream())
                .collect(Collectors.toList());
    }

    private boolean hasAnnotation(HandlerMethod handlerMethod, Class<? extends Annotation> annotationClass) {
        return handlerMethod.getMethodAnnotation(annotationClass) != null;
    }

    private List<ServerWebExchangeMatcher> createMatchersFromRequestMappingInfo(RequestMappingInfo mappingInfo) {
        List<ServerWebExchangeMatcher> matchers = new ArrayList<>();
        Set<RequestMethod> requestMethods = mappingInfo.getMethodsCondition().getMethods();
        for (String path : mappingInfo.getDirectPaths()) {
            for (RequestMethod requestMethod : requestMethods) {
                matchers.add(pathMatchers(HttpMethod.valueOf(requestMethod.name()), path));
            }
        }
        return matchers;
    }

}