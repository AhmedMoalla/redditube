package com.amoalla.redditube.gateway;


import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("routes")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
        "spring.cloud.gateway.httpclient.proxy.host=localhost",
        "spring.cloud.gateway.httpclient.proxy.port=12345"
})
class GatewayRoutesTest implements InitializingBean {

    private static MockWebServer mockWebServer;
    private WebClient webClient;

    @Value("${server.port}")
    private int gatewayPort;

    @BeforeAll
    static void setUpMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(12345);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Override
    public void afterPropertiesSet() {
        webClient = WebClient.builder()
                .baseUrl(String.format("http://localhost:%d", gatewayPort))
                .build();
    }

    @ParameterizedTest
    @CsvSource({
            "/u/test,/test,localhost:8081,true",
            "/feed,/subscriptions/feed,localhost:8082,true",
            "/subscriptions,/subscriptions,localhost:8082,false"
    })
    void testGatewayRoutes(String path, String forwardedPath, String forwardedHost, boolean skipConnectRequest) throws InterruptedException {
        webClient.get()
                .uri(path)
                .retrieve()
                .toBodilessEntity()
                .subscribe();

        if (skipConnectRequest) {
            // Skip CONNECT Request to proxy
            mockWebServer.takeRequest();
            mockWebServer.enqueue(new MockResponse());
        }
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        mockWebServer.enqueue(new MockResponse());
        assertEquals(forwardedPath, recordedRequest.getPath());
        assertEquals(forwardedHost, recordedRequest.getHeader(HttpHeaders.HOST));
    }
}
