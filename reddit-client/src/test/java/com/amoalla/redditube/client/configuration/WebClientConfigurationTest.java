package com.amoalla.redditube.client.configuration;

import com.amoalla.redditube.client.qualifier.RedditWebClient;
import com.amoalla.redditube.client.web.BearerTokenProvider;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = WebClientConfiguration.class)
@Import(WebClientConfigurationTest.RequiredBeansForTest.class)
class WebClientConfigurationTest {

    private static final String TEST_TOKEN = "TEST_TOKEN";
    private static final String TEST_RESPONSE = "TEST_RESPONSE";
    private static final String TEST_USER_AGENT = "TEST_USER_AGENT";

    @Autowired
    @RedditWebClient
    private WebClient webClient;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUpMockBackEnd() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testWebClientAddsBearerTokenToAuthorizationHeader() throws InterruptedException {

        mockWebServer.enqueue(new MockResponse().setBody(TEST_RESPONSE));
        webClient
                .get()
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(str -> assertEquals(TEST_RESPONSE, str));

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertEquals(TEST_USER_AGENT, recordedRequest.getHeader(HttpHeaders.USER_AGENT));
        assertEquals("Bearer " + TEST_TOKEN, recordedRequest.getHeader(HttpHeaders.AUTHORIZATION));
    }

    static class RequiredBeansForTest {

        private final BearerTokenProvider provider;

        RequiredBeansForTest() {
            provider = Mockito.mock(BearerTokenProvider.class);
            Mockito.when(provider.getToken()).thenReturn(TEST_TOKEN);
        }

        @Bean
        BearerTokenProvider bearerTokenProviderMock() {
            return provider;
        }

        @Bean
        WebClient.Builder webClientBuilder() {
            return WebClient.builder();
        }

        @Bean
        RedditProperties redditProperties() {
            RedditProperties properties = new RedditProperties();
            properties.setRedditOAuthBaseUrl(String.format("http://localhost:%d", mockWebServer.getPort()));
            properties.setUserAgent(TEST_USER_AGENT);
            return properties;
        }
    }
}