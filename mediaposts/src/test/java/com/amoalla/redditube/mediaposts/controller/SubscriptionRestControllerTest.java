package com.amoalla.redditube.mediaposts.controller;

import com.amoalla.redditube.commons.CommonsConfiguration;
import com.amoalla.redditube.mediaposts.dto.SubscribableDto;
import com.amoalla.redditube.mediaposts.dto.SubscriptionDto;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.entity.SubscribableType;
import com.amoalla.redditube.mediaposts.entity.Subscription;
import com.amoalla.redditube.mediaposts.service.SubscriptionService;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@WebFluxTest
@ContextConfiguration(classes = {CommonsConfiguration.class, SubscriptionRestController.class})
class SubscriptionRestControllerTest {

    private static final String TEST_USERNAME = "TEST_USERNAME";

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private SubscriptionService subscriptionService;

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    private Jwt testJwt;

    @BeforeEach
    void setUp() {
        when(subscriptionService.getFeed(TEST_USERNAME)).thenReturn(Flux.just());


        Subscribable subscribable = new Subscribable();
        subscribable.setId("ID");
        subscribable.setType(SubscribableType.USER);
        Subscription subscription = new Subscription();
        subscription.setUsername(TEST_USERNAME);
        subscription.setSubscribable(subscribable);
        when(subscriptionService.subscribe(Mockito.any(), eq(TEST_USERNAME))).thenReturn(Mono.just(subscription));
        when(subscriptionService.unsubscribe(Mockito.any(), eq(TEST_USERNAME))).thenReturn(Mono.just(subscription));
        when(subscriptionService.getSubscriptions(eq(TEST_USERNAME))).thenReturn(Flux.just());

        JSONObject json = new JSONObject();
        json.put("user_id", TEST_USERNAME);
        testJwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("federated_claims", json)
                .build();
        when(jwtDecoder.decode(anyString())).thenReturn(Mono.just(testJwt));
    }

    @Test
    void testGetFeed() {
        webClient.get()
                .uri("/subscriptions/feed")
                .headers(headers -> headers.setBearerAuth(testJwt.getTokenValue()))
                .exchange()
                .expectStatus().isOk();
        verify(subscriptionService).getFeed(TEST_USERNAME);
    }

    @Test
    void testSubscribe() {
        SubscribableDto dto = new SubscribableDto();
        dto.setId("ID");
        dto.setType(SubscribableType.USER);

        Subscribable subscribable = new Subscribable();
        subscribable.setId("ID");
        subscribable.setType(SubscribableType.USER);
        SubscriptionDto expectedSubscriptionDto = new SubscriptionDto();
        expectedSubscriptionDto.setSubscribable(subscribable);
        webClient.post()
                .uri("/subscriptions")
                .body(BodyInserters.fromValue(dto))
                .headers(headers -> headers.setBearerAuth(testJwt.getTokenValue()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SubscriptionDto.class).isEqualTo(expectedSubscriptionDto);
        verify(subscriptionService).subscribe(subscribable, TEST_USERNAME);
    }

    @Test
    void testUnsubscribe() {
        SubscribableDto dto = new SubscribableDto();
        dto.setId("ID");
        dto.setType(SubscribableType.USER);

        Subscribable subscribable = new Subscribable();
        subscribable.setId("ID");
        subscribable.setType(SubscribableType.USER);
        SubscriptionDto expectedSubscriptionDto = new SubscriptionDto();
        expectedSubscriptionDto.setSubscribable(subscribable);
        webClient.delete()
                .uri(builder -> builder.path("/subscriptions")
                        .queryParam("id", "ID")
                        .queryParam("type", "user")
                        .build())
                .headers(headers -> headers.setBearerAuth(testJwt.getTokenValue()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(SubscriptionDto.class).isEqualTo(expectedSubscriptionDto);
        verify(subscriptionService).unsubscribe(subscribable, TEST_USERNAME);
    }

    @Test
    void testGetSubscriptions() {
        webClient.get()
                .uri("/subscriptions")
                .headers(headers -> headers.setBearerAuth(testJwt.getTokenValue()))
                .exchange()
                .expectStatus().isOk();
        verify(subscriptionService).getSubscriptions(TEST_USERNAME);
    }

}