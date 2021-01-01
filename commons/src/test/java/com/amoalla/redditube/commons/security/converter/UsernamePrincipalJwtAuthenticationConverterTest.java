package com.amoalla.redditube.commons.security.converter;

import com.amoalla.redditube.commons.CommonsConfiguration;
import com.amoalla.redditube.commons.TestSecurityConfigurationRestController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.amoalla.redditube.commons.TestSecurityConfigurationRestController.TEST_SECURITY_PATH;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebFluxTest
@ActiveProfiles("oauth")
@ContextConfiguration(classes = {CommonsConfiguration.class, TestSecurityConfigurationRestController.class})
class UsernamePrincipalJwtAuthenticationConverterTest {

    private static final String TEST_USERNAME = "TEST_USERNAME";

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @Test
    void testSecurityPassThroughWhenCorrectJwtAndExtractPrincipalCorrectlyWithConverter() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim(UsernamePrincipalJwtAuthenticationConverter.PREFERRED_USERNAME_CLAIM, TEST_USERNAME)
                .build();
        when(jwtDecoder.decode(anyString())).thenReturn(Mono.just(jwt));
        webClient
                .get()
                .uri(TEST_SECURITY_PATH)
                .headers(headers -> headers.setBearerAuth(jwt.getTokenValue()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(String.format("Hello %s", TEST_USERNAME));
    }
}
