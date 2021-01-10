package com.amoalla.redditube.client.impl;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.dto.Sort;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MediaPostRequesterTest {

    private static final int TEST_LIMIT = 20;
    private static final int TEST_COUNT = 10;
    private static final String TEST_AFTER = "TEST_AFTER";
    private static final String TEST_BEFORE = "TEST_BEFORE";
    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_SUBREDDIT = "TEST_SUBREDDIT";
    private static final Sort TEST_SORT = Sort.NEW;

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
    void testRequesterSendsCorrectRequestForUserType() throws InterruptedException {
        sendRequestAndAssertItIsCorrect(MediaPostRequester.Type.USER);
    }

    @Test
    void testRequesterSendsCorrectRequestForSubredditType() throws InterruptedException {
        sendRequestAndAssertItIsCorrect(MediaPostRequester.Type.SUBREDDIT);
    }

    private void sendRequestAndAssertItIsCorrect(MediaPostRequester.Type type) throws InterruptedException {
        MediaPostRequester mediaPostRequester = createRequesterForType(type);
        String usernameOrSubreddit = MediaPostRequester.Type.USER.equals(type) ? TEST_USERNAME : TEST_SUBREDDIT;

        mockWebServer.enqueue(createMediaPostListingsMockResponse());
        Flux<MediaPostDto> mediaPostDtoFlux = mediaPostRequester
                .limit(TEST_LIMIT)
                .after(TEST_AFTER)
                .before(TEST_BEFORE)
                .count(TEST_COUNT)
                .sort(TEST_SORT)
                .sendRequest(usernameOrSubreddit)
                .log();

        StepVerifier.create(mediaPostDtoFlux)
                .verifyComplete();

        assertSentRequestIsCorrect(mockWebServer.takeRequest(), type);
    }

    private void assertSentRequestIsCorrect(RecordedRequest request, MediaPostRequester.Type type) {
        assertEquals(HttpMethod.GET.name(), request.getMethod());

        UriComponents uriComponents = UriComponentsBuilder
                .fromUri(request.getRequestUrl().uri())
                .build();

        String expectedPath;
        if (MediaPostRequester.Type.USER.equals(type)) {
            expectedPath = MediaPostRequester.USER_POSTS_URI.replace("{username}", TEST_USERNAME);
        } else {
            expectedPath = MediaPostRequester.SUBREDDIT_POSTS_URI
                    .replace("{subreddit}", TEST_SUBREDDIT)
                    .replace("{sort}", TEST_SORT.name().toLowerCase());
        }
        assertEquals(expectedPath, uriComponents.getPath());

        MultiValueMap<String, String> queryParams = uriComponents.getQueryParams();
        assertEquals(Integer.toString(TEST_LIMIT), queryParams.getFirst(MediaPostRequester.QueryParams.LIMIT));
        assertEquals(Integer.toString(TEST_COUNT), queryParams.getFirst(MediaPostRequester.QueryParams.COUNT));
        assertEquals(TEST_AFTER, queryParams.getFirst(MediaPostRequester.QueryParams.AFTER));
        assertEquals(TEST_BEFORE, queryParams.getFirst(MediaPostRequester.QueryParams.BEFORE));
        assertEquals(MediaPostRequester.REQUESTED_TYPE, queryParams.getFirst(MediaPostRequester.QueryParams.TYPE));
        if (MediaPostRequester.Type.USER.equals(type)) {
            assertEquals(TEST_SORT.name().toLowerCase(), queryParams.getFirst(MediaPostRequester.QueryParams.SORT));
        }
    }

    private MockResponse createMediaPostListingsMockResponse() {
        return new MockResponse()
                //language=JSON
                .setBody("{\"data\":  { \"children\": [] }}")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    private MediaPostRequester createRequesterForType(MediaPostRequester.Type type) {
        final ObjectMapper objectMapper = new ObjectMapper();
        InjectableValues.Std injectables = new InjectableValues.Std();
        injectables.addValue(ObjectMapper.class, objectMapper);
        objectMapper.setInjectableValues(injectables);

        final ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs()
                        .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper)))
                .build();

        WebClient webClient = WebClient.builder()
                .baseUrl(String.format("http://localhost:%d", mockWebServer.getPort()))
                .exchangeStrategies(exchangeStrategies)
                .build();
        return new MediaPostRequester(type, webClient);
    }

}