package com.amoalla.redditube.mediaposts.controller;

import com.amoalla.redditube.client.model.MediaPostDto;
import com.amoalla.redditube.mediaposts.dto.SubscribableDto;
import com.amoalla.redditube.mediaposts.dto.SubscriptionDto;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.exception.AlreadySubscribedException;
import com.amoalla.redditube.mediaposts.service.SubscriptionService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionRestController {

    private final SubscriptionService subscriptionService;
    private final ModelMapper modelMapper;

    public SubscriptionRestController(SubscriptionService subscriptionService, ModelMapper modelMapper) {
        this.subscriptionService = subscriptionService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/feed")
    public Flux<MediaPostDto> getFeed(@AuthenticationPrincipal String username) {
        return subscriptionService.getFeed(username);
    }

    @PostMapping
    public Mono<SubscriptionDto> subscribe(@Valid @RequestBody Mono<SubscribableDto> subscribableDto,
                                           @AuthenticationPrincipal String username) {

        return subscribableDto.map(dto -> modelMapper.map(subscribableDto, Subscribable.class))
                .map(subscribable -> subscriptionService.subscribe(subscribable, username))
                .map(newSubscription -> modelMapper.map(newSubscription, SubscriptionDto.class))
                .onErrorResume(AlreadySubscribedException.class, cause -> Mono.just(new SubscriptionDto(cause.getMessage())));
    }

    @DeleteMapping
    public Mono<SubscriptionDto> unsubscribe(@Valid @RequestBody Mono<SubscribableDto> subscribableDto,
                                             @AuthenticationPrincipal String username) {

        return subscribableDto.map(dto -> modelMapper.map(subscribableDto, Subscribable.class))
                .map(subscribable -> subscriptionService.unsubscribe(subscribable, username))
                .map(newSubscription -> modelMapper.map(newSubscription, SubscriptionDto.class));
    }

    @GetMapping
    public Flux<SubscribableDto> getSubscriptions(@AuthenticationPrincipal String username) {

        return subscriptionService.getSubscriptions(username)
                .map(subscribable -> modelMapper.map(subscribable, SubscribableDto.class));
    }
}
