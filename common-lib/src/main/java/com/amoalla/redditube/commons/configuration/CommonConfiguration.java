package com.amoalla.redditube.commons.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan(basePackages = "com.amoalla.redditube.commons")
public class CommonConfiguration {

    @Bean
    public CommandLineRunner initCommonConfig(){
        return args->{log.info("Init Common configuration");};
    }
}
