package com.amoalla.redditube.gateway.controller;

import com.amoalla.redditube.gateway.dto.RegisteredUserDto;
import com.amoalla.redditube.gateway.dto.RegistrationResponseDto;
import com.amoalla.redditube.gateway.dto.UserDto;
import com.amoalla.redditube.gateway.entity.RedditubeUser;
import com.amoalla.redditube.gateway.exception.UserAlreadyExistsException;
import com.amoalla.redditube.gateway.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserRestController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public Mono<RegistrationResponseDto> registerUser(@Valid @RequestBody Mono<UserDto> userDto) throws UserAlreadyExistsException {
        return userDto.map(dto -> Tuples.of(modelMapper.map(dto, RedditubeUser.class), dto.getPassword()))
                .flatMap(tuple -> userService.registerUser(tuple.getT1(), tuple.getT2()))
                .map(newUser -> modelMapper.map(newUser, RegisteredUserDto.class))
                .map(RegistrationResponseDto::new)
                .onErrorResume(WebExchangeBindException.class, e -> Mono.just(new RegistrationResponseDto(e.getAllErrors())))
                .onErrorResume(UserAlreadyExistsException.class, e -> Mono.just(new RegistrationResponseDto(e.getMessage())));
    }
}
