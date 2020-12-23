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

/**
 * Service responsible for retrieving and refreshing an access token.
 * An access token is required in each request in the Authorization Header.
 * The access token is retrieved on application startup and is refreshed
 * just before it expires.
 */
@Slf4j
@Service
public class BearerTokenProvider implements InitializingBean {

    public static final String ACCESS_TOKEN_URI = "/api/v1/access_token";
    public static final String GRANT_TYPE = "grant_type";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private final WebClient webClient;
    private final RedditProperties properties;

    private String token;
    private int timeToLiveInSeconds;
    private Instant tokenCreationTime;

    // Used to block getToken() until a value is provided asynchronously in afterPropertiesSet()
    private final CountDownLatch latch = new CountDownLatch(1);

    public BearerTokenProvider(WebClient.Builder builder, RedditProperties properties) {
        this.webClient = builder
                .baseUrl(properties.getRedditBaseUrl())
                .defaultHeader(HttpHeaders.USER_AGENT, properties.getUserAgent())
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

        long checkRefreshPeriod = properties.getCheckRefreshPeriod();
        log.info("Starting refresh token task <Period: {} minutes>", checkRefreshPeriod / 60);
        Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(refreshRunnable, checkRefreshPeriod, checkRefreshPeriod, TimeUnit.SECONDS);
    }

    private boolean isTokenNearlyExpired() {
        Instant now = Instant.now();
        Duration earlyFetchDuration = Duration.ofSeconds(properties.getCheckRefreshPeriod() + 1);
        Duration lifetime = Duration.between(tokenCreationTime, now)
                .plus(earlyFetchDuration);
        return lifetime.getSeconds() > timeToLiveInSeconds;
    }
}
