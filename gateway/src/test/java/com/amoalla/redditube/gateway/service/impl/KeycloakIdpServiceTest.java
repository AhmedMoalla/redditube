package com.amoalla.redditube.gateway.service.impl;

import com.amoalla.redditube.gateway.entity.RedditubeUser;
import com.amoalla.redditube.gateway.exception.UserRegistrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = KeycloakIdpService.class)
class KeycloakIdpServiceTest {

    @MockBean
    private UsersResource usersResource;

    @Autowired
    private KeycloakIdpService keycloakIdpService;

    private RedditubeUser testUser;

    @BeforeEach
    void setUp() throws URISyntaxException {
        when(usersResource.create(Mockito.any())).thenReturn(Response.created(new URI("/users/1")).build());

        testUser = new RedditubeUser();
        testUser.setUsername("TEST_USERNAME");
        testUser.setEmail("TEST_EMAIL");
        testUser.setFirstName("TEST_FIRSTNAME");
        testUser.setLastName("TEST_LASTNAME");
    }

    @Test
    void testRegisterUser() {

        ArgumentCaptor<UserRepresentation> argumentCaptor = ArgumentCaptor.forClass(UserRepresentation.class);
        String password = "TEST_PASSWORD";
        Mono<RedditubeUser> returnedUser = keycloakIdpService.registerUser(testUser, password);

        verify(usersResource).create(argumentCaptor.capture());
        UserRepresentation userRepresentation = argumentCaptor.getValue();
        assertEquals(testUser.getUsername(), userRepresentation.getUsername());
        assertEquals(testUser.getEmail(), userRepresentation.getEmail());
        assertEquals(testUser.getFirstName(), userRepresentation.getFirstName());
        assertEquals(testUser.getLastName(), userRepresentation.getLastName());
        assertEquals(password, userRepresentation.getCredentials().get(0).getValue());

        StepVerifier.create(returnedUser)
                .expectNext(testUser)
                .expectComplete()
                .verify();
    }

    @Test
    void testRegisterUserThrowsUserRegistrationException() {
        when(usersResource.create(Mockito.any())).thenReturn(Response.serverError().build());

        ArgumentCaptor<UserRepresentation> argumentCaptor = ArgumentCaptor.forClass(UserRepresentation.class);
        String password = "TEST_PASSWORD";
        UserRegistrationException ex = assertThrows(UserRegistrationException.class,
                () -> keycloakIdpService.registerUser(testUser, password));

        verify(usersResource).create(argumentCaptor.capture());
        UserRepresentation userRepresentation = argumentCaptor.getValue();
        assertEquals(testUser.getUsername(), userRepresentation.getUsername());
        assertEquals(testUser.getEmail(), userRepresentation.getEmail());
        assertEquals(testUser.getFirstName(), userRepresentation.getFirstName());
        assertEquals(testUser.getLastName(), userRepresentation.getLastName());
        assertEquals(password, userRepresentation.getCredentials().get(0).getValue());
    }

}