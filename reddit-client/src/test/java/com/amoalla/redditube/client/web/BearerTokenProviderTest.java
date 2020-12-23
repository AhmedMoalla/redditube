package com.amoalla.redditube.client.web;

import com.amoalla.redditube.client.configuration.RedditProperties;
import com.amoalla.redditube.client.impl.MediaPostRequester;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BearerTokenProviderTest {

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_PASSWORD = "TEST_PASSWORD";
    private static final String TEST_CLIENT_ID = "TEST_CLIENT_ID";
    private static final String TEST_CLIENT_SECRET = "TEST_CLIENT_SECRET";
    private static final MediaPostRequester.Type TEST_TYPE = MediaPostRequester.Type.USER;

    private static final String TEST_ACCESS_TOKEN = "ACCESS_TOKEN";

    private static MockWebServer mockBackEnd;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private BearerTokenProvider bearerTokenProvider;

    @BeforeAll
    static void setUpMockBackEnd() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void setUp() {
        RedditProperties properties = new RedditProperties();
        properties.setRedditBaseUrl(String.format("http://localhost:%d", mockBackEnd.getPort()));
        properties.setUsername(TEST_USERNAME);
        properties.setPassword(TEST_PASSWORD);
        properties.setClientId(TEST_CLIENT_ID);
        properties.setClientSecret(TEST_CLIENT_SECRET);
        properties.setType(TEST_TYPE);
        properties.setCheckRefreshPeriod(1L);

        bearerTokenProvider = new BearerTokenProvider(WebClient.builder(), properties);
    }

    @Test
    void testProviderObtainsTokenOnPropertiesSet() throws JsonProcessingException, InterruptedException {

        MockResponse response = createAccessTokenMockResponse(TEST_ACCESS_TOKEN);
        mockBackEnd.enqueue(response);
        bearerTokenProvider.afterPropertiesSet();

        assertEquals(TEST_ACCESS_TOKEN, bearerTokenProvider.getToken());

        assertSentRequestIsCorrect(mockBackEnd.takeRequest());
    }

    @Test
    void testRefreshTokenTask() throws JsonProcessingException, InterruptedException {

        MockResponse response = createAccessTokenMockResponse(TEST_ACCESS_TOKEN);
        mockBackEnd.enqueue(response);
        bearerTokenProvider.afterPropertiesSet();

        mockBackEnd.takeRequest();
        assertEquals(1, mockBackEnd.getRequestCount());
        assertEquals(TEST_ACCESS_TOKEN, bearerTokenProvider.getToken());

        String newAccessToken = TEST_ACCESS_TOKEN + "2";
        response = createAccessTokenMockResponse(newAccessToken);
        mockBackEnd.enqueue(response);
        mockBackEnd.takeRequest();
        assertEquals(2, mockBackEnd.getRequestCount());
        await("New Access Token")
                .atMost(1, TimeUnit.SECONDS)
                .until(() -> newAccessToken.equals(bearerTokenProvider.getToken()));
        assertEquals(newAccessToken, bearerTokenProvider.getToken());
    }

    private void assertSentRequestIsCorrect(RecordedRequest request) {
        assertEquals(HttpMethod.POST.name(), request.getMethod());
        assertEquals(BearerTokenProvider.ACCESS_TOKEN_URI, request.getPath());

        String credentials = String.format("%s:%s", TEST_CLIENT_ID, TEST_CLIENT_SECRET);
        String expectAuthHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
        assertEquals(expectAuthHeader, request.getHeader(HttpHeaders.AUTHORIZATION));

        assertTrue(request.getHeader(HttpHeaders.CONTENT_TYPE).contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE));


        MultiValueMap<String, String> queryParams = UriComponentsBuilder.newInstance()
                .query(request.getBody().readUtf8())
                .build().getQueryParams();
        assertEquals(BearerTokenProvider.GRANT_TYPE_PASSWORD, queryParams.getFirst(BearerTokenProvider.GRANT_TYPE));
        assertEquals(TEST_USERNAME, queryParams.getFirst(BearerTokenProvider.USERNAME));
        assertEquals(TEST_PASSWORD, queryParams.getFirst(BearerTokenProvider.PASSWORD));
    }

    private MockResponse createAccessTokenMockResponse(String tokenValue) throws JsonProcessingException {
        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken(tokenValue);
        accessToken.setExpiresInSeconds(2);

        return new MockResponse()
                .setBody(objectMapper.writeValueAsString(accessToken))
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }
}