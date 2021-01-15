package com.amoalla.redditube.commons;

import com.amoalla.redditube.commons.configuration.ServiceHostsProperties;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Slf4j
@Configuration
@ComponentScan(basePackages = "com.amoalla.redditube.commons")
@EnableConfigurationProperties(ServiceHostsProperties.class)
public class CommonsConfiguration {

    @Bean
    public CommandLineRunner initCommonsConfig(Environment env) {
        boolean securityEnabled = Arrays.asList(env.getActiveProfiles()).contains("oauth");
        return args -> log.info("Init Commons configuration. Security: {}", securityEnabled);
    }

    @Bean
    public ModelMapper provideModelMapper() {
        return new ModelMapper();
    }
}
