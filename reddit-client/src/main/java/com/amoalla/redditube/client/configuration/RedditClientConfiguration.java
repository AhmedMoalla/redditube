package com.amoalla.redditube.client.configuration;

import com.amoalla.redditube.client.RedditClient;
import com.amoalla.redditube.client.impl.MediaPostRequester;
import com.amoalla.redditube.client.impl.RedditMediaClientImpl;
import com.amoalla.redditube.client.qualifier.RedditWebClient;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Configuration
@ComponentScan(basePackages = "com.amoalla.redditube.client")
@ConditionalOnProperty("reddit.client-id")
@EnableConfigurationProperties(RedditProperties.class)
public class RedditClientConfiguration {

    @Bean
    CommandLineRunner libInit(RedditClient redditClient, RedditProperties properties) {
        return args ->
                log.info("Configured Reddit client implementation: {} with URI: {}",
                        redditClient.getClass().getName(), properties.getType().getUri());
    }

    @Bean
    RedditClient provideRedditClient(RedditProperties properties, @RedditWebClient WebClient webClient) {
        MediaPostRequester requester = new MediaPostRequester(properties.getType(), webClient);
        return new RedditMediaClientImpl(requester);
    }

    @Bean
    ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
        InjectableValues.Std injectables = new InjectableValues.Std();
        injectables.addValue(ObjectMapper.class, objectMapper);
        objectMapper.setInjectableValues(injectables);
        return objectMapper;
    }
}