package com.amoalla.redditube.gateway.configuration;

import com.amoalla.redditube.gateway.service.IdpService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class GatewayConfigurationTest {

    @Test
    void testPasswordEncoderIsPresentInContext() {
        new ApplicationContextRunner()
                .withUserConfiguration(GatewayConfiguration.class)
                .withPropertyValues(
                        "redditube.idp.host=localhost",
                        "redditube.idp.port=5555",
                        "redditube.idp.realm=redditube",
                        "redditube.idp.client-id=CLIENT_ID",
                        "redditube.idp.client-secret=CLIENT_SECRET")
                .run(context -> {
                    assertThat(context).hasSingleBean(PasswordEncoder.class);
                    assertThat(context).hasSingleBean(IdpService.class);
                });
    }
}