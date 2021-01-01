package com.amoalla.redditube.gateway.configuration;

import com.amoalla.redditube.gateway.configuration.properties.IdpProperties;
import com.amoalla.redditube.gateway.service.IdpService;
import com.amoalla.redditube.gateway.service.impl.KeycloakIdpService;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties(IdpProperties.class)
public class GatewayConfiguration {

    @Bean
    PasswordEncoder provideBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    IdpService provideIdpService(IdpProperties idpProperties) {
        String serverUrl = String.format("http://%s:%d/auth", idpProperties.getHost(), idpProperties.getPort());
        UsersResource userResource = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(idpProperties.getRealm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(idpProperties.getClientId())
                .clientSecret(idpProperties.getClientSecret())
                .build()
                .realm(idpProperties.getRealm())
                .users();
        return new KeycloakIdpService(userResource);
    }

}
