package com.amoalla.redditube.gateway.service;

import com.amoalla.redditube.gateway.entity.RedditubeUser;
import com.amoalla.redditube.gateway.exception.EmailAlreadyExistsException;
import com.amoalla.redditube.gateway.exception.UsernameAlreadyExistsException;
import com.amoalla.redditube.gateway.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UserService.class)
class UserServiceTest {

    private static final String TEST_USERNAME_EXISTS = "TEST_USERNAME_EXISTS";
    private static final String TEST_USERNAME_NOT_EXISTS = "TEST_USERNAME_NOT_EXISTS";
    private static final String TEST_EMAIL_EXISTS = "user@mail.com";
    private static final String TEST_EMAIL_NOT_EXISTS = "user.not.exists@mail.com";
    private static final String TEST_PASSWORD = "TEST_PASSWORD";

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DexService dexService;

    @Autowired
    private UserService userService;

    private RedditubeUser testUser;

    @BeforeEach
    void setUp() {
        when(userRepository.existsById(TEST_USERNAME_EXISTS)).thenReturn(true);
        when(userRepository.existsById(TEST_USERNAME_NOT_EXISTS)).thenReturn(false);
        when(userRepository.existsByEmail(TEST_EMAIL_EXISTS)).thenReturn(true);
        when(userRepository.existsByEmail(TEST_EMAIL_NOT_EXISTS)).thenReturn(false);

        testUser = new RedditubeUser();
        testUser.setUsername(TEST_USERNAME_NOT_EXISTS);
        when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }

    @Test
    void testRegisterUser() {
        Mono<RedditubeUser> user = userService.registerUser(testUser, TEST_PASSWORD);
        verify(userRepository).save(testUser);
        verify(dexService).createNewPassword(testUser, TEST_PASSWORD);
        StepVerifier.create(user)
                .expectNext(testUser)
                .expectComplete()
                .verify();
    }

    @Test
    void testRegisterUserThrowsUsernameAlreadyExistsException() {
        testUser.setUsername(TEST_USERNAME_EXISTS);
        assertThrows(UsernameAlreadyExistsException.class, () -> userService.registerUser(testUser, TEST_PASSWORD));
    }

    @Test
    void testRegisterUserThrowsEmailAlreadyExistsExceptionException() {
        testUser.setEmail(TEST_EMAIL_EXISTS);
        assertThrows(EmailAlreadyExistsException.class, () -> userService.registerUser(testUser, TEST_PASSWORD));
    }

}