package com.amoalla.redditube.commons;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.amoalla.redditube.commons.TestSecurityConfigurationRestController.TEST_SECURITY_PATH;
import static com.amoalla.redditube.commons.TestSecurityConfigurationRestController.TEST_SHOULD_BE_UNSECURE;

@WebFluxTest
@ActiveProfiles("oauth")
@ContextConfiguration(classes = {CommonsConfiguration.class, TestSecurityConfigurationRestController.class})
class CommonsConfigurationWithSecurityTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @Test
    void testSecurityIsOnWhenProfileIsSet() {
        webClient.get()
                .uri(TEST_SECURITY_PATH)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testSecurityIsOffWithPermitAllAnnotation() {
        webClient.get()
                .uri(TEST_SHOULD_BE_UNSECURE)
                .exchange()
                .expectStatus().isOk();
    }
}