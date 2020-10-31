package com.amoalla.redditube.client.web;

import com.amoalla.redditube.client.configuration.RedditProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.amoalla.redditube.client.configuration.WebClientConfiguration.USER_AGENT_VALUE;

/**
 * Service responsible for retrieving and refreshing an access token.
 * An access token is required in each request in the Authorization Header.
 * The access token is retrieved on application startup and is refreshed
 * just before it expires.
 */
@Slf4j
@Service
public class BearerTokenProvider implements InitializingBean {

    private static final String REDDIT_BASE_URL = "https://www.reddit.com";
    private static final String ACCESS_TOKEN_URI = "/api/v1/access_token";
    private static final int REMAINING_LIFETIME_TO_REFRESH = 15;
    private static final Duration EARLY_FETCH_DURATION = Duration.ofMinutes(REMAINING_LIFETIME_TO_REFRESH + 1);

    private static final String GRANT_TYPE = "grant_type";
    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private final WebClient webClient;
    private final RedditProperties properties;

    private String token;
    private int timeToLiveInSeconds;
    private Instant tokenCreationTime;

    // Used to block getToken() until a value is provided asynchronously in afterPropertiesSet()
    private final CountDownLatch latch = new CountDownLatch(1);

    public BearerTokenProvider(WebClient.Builder builder, RedditProperties properties) {
        this.webClient = builder
                .baseUrl(REDDIT_BASE_URL)
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT_VALUE)
                .build();
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        // Retrieve first token
        obtainAccessToken().subscribe(accessToken -> {
            setToken(accessToken);
            latch.countDown();
            startRefreshTokenTask();
        });
    }

    @SneakyThrows
    public String getToken() {
        // If called before setToken() in afterPropertiesSet()
        // Wait until it's available
        if (StringUtils.isEmpty(token)) {
            log.warn("Token was needed before it was set. Waiting for token to be retrieved...");
            latch.await();
        }

        return token;
    }

    private Mono<AccessToken> obtainAccessToken() {
        return webClient.post()
                .uri(ACCESS_TOKEN_URI)
                .headers(headers -> headers.setBasicAuth(properties.getClientId(), properties.getClientSecret()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(GRANT_TYPE, GRANT_TYPE_PASSWORD)
                        .with(USERNAME, properties.getUsername())
                        .with(PASSWORD, properties.getPassword()))
                .retrieve()
                .bodyToMono(AccessToken.class)
                .doOnNext(token -> log.info("Token obtained from Reddit API: {}", token));
    }

    private void setToken(AccessToken accessToken) {
        tokenCreationTime = Instant.now();
        token = accessToken.getAccessToken();
        timeToLiveInSeconds = accessToken.getExpiresInSeconds();
    }

    private void startRefreshTokenTask() {
        Runnable refreshRunnable = () -> {
            log.debug("Checking if token is nearly expired...");
            if (isTokenNearlyExpired()) {
                log.debug("Token is nearly expired. Refreshing token...");
                obtainAccessToken().subscribe(this::setToken);
            }
        };

        log.info("Starting refresh token task <Period: {} minutes>", REMAINING_LIFETIME_TO_REFRESH);
        Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(refreshRunnable, REMAINING_LIFETIME_TO_REFRESH, REMAINING_LIFETIME_TO_REFRESH, TimeUnit.MINUTES);
    }

    private boolean isTokenNearlyExpired() {
        Instant now = Instant.now();
        Duration lifetime = Duration.between(tokenCreationTime, now)
                .plus(EARLY_FETCH_DURATION);
        return lifetime.getSeconds() > timeToLiveInSeconds;
    }
}
