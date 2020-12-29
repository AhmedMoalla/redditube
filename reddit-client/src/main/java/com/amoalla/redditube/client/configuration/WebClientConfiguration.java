package com.amoalla.redditube.client.configuration;

import com.amoalla.redditube.client.web.BearerTokenProvider;
import com.amoalla.redditube.client.qualifier.RedditWebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.HttpHeaders.USER_AGENT;


@Configuration
public class WebClientConfiguration {

    private static final int BUFFER_SIZE = 16 * 1024 * 1024;

    private final BearerTokenProvider provider;
    private final String userAgent;
    private final String redditOAuthBaseUrl;

    public WebClientConfiguration(BearerTokenProvider provider, RedditProperties properties) {
        this.provider = provider;
        userAgent = properties.getUserAgent();
        redditOAuthBaseUrl = properties.getRedditOAuthBaseUrl();
    }

    @Bean
    @RedditWebClient
    WebClient provideRedditWebClient(WebClient.Builder builder) {
        ExchangeStrategies bufferSizeStrategy = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(BUFFER_SIZE))
                .build();

        return builder
                .exchangeStrategies(bufferSizeStrategy)
                .defaultHeader(USER_AGENT, userAgent)
                .baseUrl(redditOAuthBaseUrl)
                .filter(authHeader())
                .build();
    }

    private ExchangeFilterFunction authHeader() {
        return (request, next) -> next.exchange(
                ClientRequest.from(request)
                        .headers(headers -> headers.setBearerAuth(provider.getToken()))
                        .build());
    }

}
