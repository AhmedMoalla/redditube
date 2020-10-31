package com.amoalla.redditube.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackRestController {

    @GetMapping
    public Mono<String> fallback() {
        return Mono.just("fallback");
    }
}
