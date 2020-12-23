package com.amoalla.redditube.client.configuration;

import com.amoalla.redditube.client.RedditClient;
import com.amoalla.redditube.client.web.BearerTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class RedditClientConfigurationProvidesRedditClientTest {

    @Test
    void testRedditClientBeanPresentInContext() {
        new ApplicationContextRunner()
                .withPropertyValues(
                        "reddit.client-id='CLIENT_ID'",
                        "reddit.client-secret='CLIENT_SECRET'",
                        "reddit.username='USERNAME'",
                        "reddit.password='PASSWORD'",
                        "reddit.type=User")
                .withConfiguration(AutoConfigurations.of(RedditClientConfiguration.class))
                .withUserConfiguration(RequiredBeansForTest.class)
                .run(context -> assertThat(context).hasSingleBean(RedditClient.class));
    }

    @Test
    void testRedditClientBeanAbsentInContextWhenNoClientId() {
        new ApplicationContextRunner()
                .withPropertyValues(
                        "reddit.client-secret='CLIENT_SECRET'",
                        "reddit.username='USERNAME'",
                        "reddit.password='PASSWORD'",
                        "reddit.type=User")
                .withConfiguration(AutoConfigurations.of(RedditClientConfiguration.class))
                .withUserConfiguration(RequiredBeansForTest.class)
                .run(context -> {
                    assertThat(context).doesNotHaveBean(RedditClient.class);
                    assertThat(context).doesNotHaveBean(BearerTokenProvider.class);
                    assertThat(context).doesNotHaveBean(WebClient.class);
                });
    }

    static class RequiredBeansForTest {

        @Bean
        WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }

        @Bean
        @Primary
        ObjectMapper baseObjectMapper() {
            return new ObjectMapper();
        }
    }

}
