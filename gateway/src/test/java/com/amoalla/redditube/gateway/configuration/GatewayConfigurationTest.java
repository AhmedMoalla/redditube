package com.amoalla.redditube.gateway.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class GatewayConfigurationTest {

    @Test
    void testPasswordEncoderIsPresentInContext() {
        new ApplicationContextRunner()
                .withUserConfiguration(GatewayConfiguration.class)
                .run(context -> assertThat(context).hasSingleBean(PasswordEncoder.class));
    }
}