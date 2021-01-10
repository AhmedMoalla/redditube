package com.amoalla.redditube.client.configuration;

import com.amoalla.redditube.client.RedditClient;
import com.amoalla.redditube.client.impl.MediaPostRequester;
import com.amoalla.redditube.client.impl.RedditMediaClientImpl;
import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.client.model.deserializer.MediaPostDtoDeserializer;
import com.amoalla.redditube.client.qualifier.RedditWebClient;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
    public ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
        SimpleModule module = new SimpleModule("MediaPostDtoDeserializer", new Version(1, 0, 0, null, null, null));
        module.addDeserializer(MediaPostDto.class, new MediaPostDtoDeserializer());
        objectMapper.registerModule(module);

        InjectableValues.Std injectables = new InjectableValues.Std();
        injectables.addValue(ObjectMapper.class, objectMapper);
        objectMapper.setInjectableValues(injectables);
        return objectMapper;
    }
}