package com.amoalla.redditube.commons.security.converter;

import net.minidev.json.JSONObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

/**
 * A custom AuthenticationConverter used to set the principal on the Authentication Object as the username
 */
public class DexUserIdPrincipalJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    private static final String FEDERATED_CLAIMS = "federated_claims";
    private static final String USER_ID_CLAIM = "user_id";
    private final String errorMessage = "Claim '%s' could not be found on JWT. " +
            "Maybe you are not using Dex anymore. " +
            "You need to reimplement " + getClass().getName() + ".getUserIdPrincipal(Jwt jwt) if it's the case";

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt source) {
        String username = getUserIdPrincipal(source);
        return Mono.just(new UserIdPrincipalJwtAuthenticationToken(source, username));
    }

    // This implementation will only work for Dex. Need to have a more Generic implementation
    private String getUserIdPrincipal(Jwt jwt) {
        JSONObject object = (JSONObject) jwt.getClaims().get(FEDERATED_CLAIMS);
        if (object == null) {
            throw new IllegalArgumentException(String.format(errorMessage, FEDERATED_CLAIMS));
        }

        String userId = object.getAsString(USER_ID_CLAIM);
        if (userId == null) {
            throw new IllegalArgumentException(String.format(errorMessage, USER_ID_CLAIM));
        }

        return userId;
    }

    private static class UserIdPrincipalJwtAuthenticationToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

        public UserIdPrincipalJwtAuthenticationToken(Jwt token, String userId) {
            super(token, userId, token, Collections.emptyList());
            this.setAuthenticated(true);
        }

        @Override
        public Map<String, Object> getTokenAttributes() {
            return this.getToken().getClaims();
        }
    }

}
