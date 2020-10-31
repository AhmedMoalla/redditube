package com.amoalla.redditube.gateway.configuration;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class GatewayConfiguration {

    @Bean
    PasswordEncoder provideBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    ModelMapper provideModelMapper() {
        return new ModelMapper();
    }
}
