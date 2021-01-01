package com.amoalla.redditube.commons.security.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;

/**
 * A custom AuthenticationConverter used to set the principal on the Authentication Object as the username
 */
public class UsernamePrincipalJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    static final String PREFERRED_USERNAME_CLAIM = "preferred_username";
    private final String errorMessage = "Claim '%s' could not be found on JWT. " +
            "Maybe you are not using Keycloak anymore. " +
            "You need to reimplement " + getClass().getName() + ".convert(Jwt jwt) if it's the case";

    private final Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter
            = new JwtGrantedAuthoritiesConverter();

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
        String username = jwt.getClaim(PREFERRED_USERNAME_CLAIM);
        if (!StringUtils.hasText(username)) {
            throw new IllegalArgumentException(String.format(errorMessage, PREFERRED_USERNAME_CLAIM));
        }
        return Mono.just(new UsernamePrincipalJwtAuthenticationToken(jwt, username, authorities));
    }

    private static class UsernamePrincipalJwtAuthenticationToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> {

        public UsernamePrincipalJwtAuthenticationToken(Jwt token, String username, Collection<GrantedAuthority> authorities) {
            super(token, username, token, authorities);
            this.setAuthenticated(true);
        }

        @Override
        public Map<String, Object> getTokenAttributes() {
            return this.getToken().getClaims();
        }
    }
}
