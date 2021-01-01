package com.amoalla.redditube.gateway.controller;

import com.amoalla.redditube.gateway.dto.RegisteredUserDto;
import com.amoalla.redditube.gateway.dto.RegistrationResponseDto;
import com.amoalla.redditube.gateway.dto.UserDto;
import com.amoalla.redditube.gateway.entity.RedditubeUser;
import com.amoalla.redditube.gateway.exception.EmailAlreadyExistsException;
import com.amoalla.redditube.gateway.exception.UserRegistrationException;
import com.amoalla.redditube.gateway.exception.UsernameAlreadyExistsException;
import com.amoalla.redditube.gateway.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserRestController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PermitAll
    @PostMapping
    public Mono<RegistrationResponseDto> registerUser(@Valid @RequestBody Mono<UserDto> userDto) {

        log.info("Received POST /users with data: {}", userDto);
        return userDto.map(dto -> Tuples.of(modelMapper.map(dto, RedditubeUser.class), dto.getPassword()))
                .flatMap(tuple -> userService.registerUser(tuple.getT1(), tuple.getT2()))
                .map(newUser -> modelMapper.map(newUser, RegisteredUserDto.class))
                .map(RegistrationResponseDto::new)
                .onErrorResume(WebExchangeBindException.class, e -> Mono.just(new RegistrationResponseDto(e.getAllErrors())))
                .onErrorResume(UsernameAlreadyExistsException.class, e -> Mono.just(new RegistrationResponseDto(e.getMessage())))
                .onErrorResume(EmailAlreadyExistsException.class, e -> Mono.just(new RegistrationResponseDto(e.getMessage())))
                .onErrorResume(UserRegistrationException.class, e -> Mono.just(new RegistrationResponseDto(e.getMessage())));
    }
}
