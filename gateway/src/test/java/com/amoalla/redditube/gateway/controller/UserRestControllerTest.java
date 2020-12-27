package com.amoalla.redditube.gateway.controller;

import com.amoalla.redditube.commons.CommonsConfiguration;
import com.amoalla.redditube.gateway.dto.RegisteredUserDto;
import com.amoalla.redditube.gateway.dto.RegistrationResponseDto;
import com.amoalla.redditube.gateway.dto.UserDto;
import com.amoalla.redditube.gateway.entity.RedditubeUser;
import com.amoalla.redditube.gateway.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebFluxTest
@ActiveProfiles("default")
@ContextConfiguration(classes = {CommonsConfiguration.class, UserRestController.class})
class UserRestControllerTest {

    private static final String TEST_EMAIL = "test_user@mail.com";
    private static final String TEST_FIRSTNAME = "test_firstname";
    private static final String TEST_LASTNAME = "test_lastname";

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        RedditubeUser user = new RedditubeUser();
        user.setEmail(TEST_EMAIL);
        user.setFirstName(TEST_FIRSTNAME);
        user.setLastName(TEST_LASTNAME);
        when(userService.registerUser(Mockito.any(), Mockito.anyString())).thenReturn(Mono.just(user));
    }

    @Test
    void testRegisterUser() {
        RegisteredUserDto registeredUserDto = new RegisteredUserDto();
        registeredUserDto.setEmail(TEST_EMAIL);
        registeredUserDto.setFirstName(TEST_FIRSTNAME);
        registeredUserDto.setLastName(TEST_LASTNAME);
        RegistrationResponseDto expectedRegistration = new RegistrationResponseDto(registeredUserDto);

        UserDto userDto = new UserDto();
        userDto.setEmail(TEST_EMAIL);
        userDto.setFirstName(TEST_FIRSTNAME);
        userDto.setLastName(TEST_LASTNAME);
        userDto.setPassword("Test_password1");
        userDto.setConfirmPassword("Test_password1");

        webClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(RegistrationResponseDto.class).isEqualTo(expectedRegistration);
    }

    @Test
    void testRegisterUserShouldReturnRegistrationResponseWithErrors() {
        webClient.post()
                .uri("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new UserDto()))
                .exchange()
                .expectStatus().isOk()
                .expectBody(RegistrationResponseDto.class)
                .value(registrationDto -> assertFalse(registrationDto.getErrors().isEmpty()));
    }
}