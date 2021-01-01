package com.amoalla.redditube.gateway.service.impl;

import com.amoalla.redditube.gateway.entity.RedditubeUser;
import com.amoalla.redditube.gateway.exception.UserRegistrationException;
import com.amoalla.redditube.gateway.exception.UsernameAlreadyExistsException;
import com.amoalla.redditube.gateway.service.IdpService;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import reactor.core.publisher.Mono;

import javax.ws.rs.core.Response;
import java.util.Collections;

public class KeycloakIdpService implements IdpService {

    private final UsersResource userResource;

    public KeycloakIdpService(UsersResource userResource) {
        this.userResource = userResource;
    }

    @Override
    public Mono<RedditubeUser> registerUser(RedditubeUser user, String rawPassword) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setValue(rawPassword);
        credential.setTemporary(false);
        userRepresentation.setCredentials(Collections.singletonList(credential));
        try (Response response = userResource.create(userRepresentation)) {
            if (Response.Status.CONFLICT.equals(response.getStatusInfo().toEnum())) {
                throw new UsernameAlreadyExistsException(user.getUsername());
            }
            String createdId = CreatedResponseUtil.getCreatedId(response);
            user.setIdpId(createdId);
        } catch (Exception e) {
            throw new UserRegistrationException(e);
        }
        return Mono.just(user);
    }
}
