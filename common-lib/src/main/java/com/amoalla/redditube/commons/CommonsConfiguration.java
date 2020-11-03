package com.amoalla.redditube.commons;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@ComponentScan(basePackages = "com.amoalla.redditube.commons")
public class CommonsConfiguration {

    @Autowired
    private Utils utils;

    @Bean
    public CommandLineRunner initCommonConfig(){

        return args->{log.info("Init Commons configuration, Security {}", utils.isProfileActive(RedditConstants.SECURITY_ENABLED_PROFILE));};
    }

    @Bean
    public Utils getUtilsBean(){
        return new Utils();
    }
}
