package com.amoalla.redditube.commons;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestSecurityConfigurationRestController {
    public static final String TEST_SECURITY_PATH = "/test-security";
    public static final String TEST_UNSECURE_PATH = "/unsecure";

    @GetMapping(TEST_SECURITY_PATH)
    Mono<String> sayHello(@AuthenticationPrincipal String username) {
        return Mono.just(String.format("Hello %s", username));
    }

    @GetMapping(TEST_UNSECURE_PATH)
    Mono<String> sayHelloWorld() {
        return Mono.just("Hello World");
    }
}