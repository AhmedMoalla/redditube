package com.amoalla.redditube.commons;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.amoalla.redditube.commons.TestSecurityConfigurationRestController.TEST_UNSECURE_PATH;

@WebFluxTest
@ActiveProfiles("default")
@ContextConfiguration(classes = {CommonsConfiguration.class, TestSecurityConfigurationRestController.class})
class CommonsConfigurationWithoutSecurityTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void testSecurityIsOffWhenProfileIsNotSet() {
        webClient.get()
                .uri(TEST_UNSECURE_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello World");
    }
}
