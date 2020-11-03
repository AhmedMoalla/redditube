package com.amoalla.redditube.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Slf4j
@Configuration
@ComponentScan(basePackages = "com.amoalla.redditube.commons")
public class CommonsConfiguration {

    @Bean
    public CommandLineRunner initCommonsConfig(Environment env) {
        boolean securityEnabled = Arrays.asList(env.getActiveProfiles()).contains("oauth");
        return args -> log.info("Init Commons configuration. Security: {}", securityEnabled);
    }
}
