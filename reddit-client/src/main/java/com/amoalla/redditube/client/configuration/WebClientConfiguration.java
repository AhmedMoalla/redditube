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

    public static final String USER_AGENT_VALUE = "Redditube (by Aerodash)";
    public static final String OAUTH_BASE_URL = "https://oauth.reddit.com";

    private static final int BUFFER_SIZE = 16 * 1024 * 1024;

    private final BearerTokenProvider provider;

    public WebClientConfiguration(BearerTokenProvider provider) {
        this.provider = provider;
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
                .defaultHeader(USER_AGENT, USER_AGENT_VALUE)
                .baseUrl(OAUTH_BASE_URL)
                .filter(authHeader())
                .build();
    }

    private ExchangeFilterFunction authHeader() {
        return (request, next) -> next.exchange(
                ClientRequest.from(request)
                        .headers((headers) -> headers.setBearerAuth(provider.getToken()))
                        .build());
    }

}
