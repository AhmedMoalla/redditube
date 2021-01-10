package com.amoalla.redditube.commons;

import com.amoalla.redditube.commons.configuration.ServiceHostsProperties;
import com.amoalla.redditube.commons.modelmapper.LocalDateTimeToProtoTimestampConverter;
import com.amoalla.redditube.commons.modelmapper.ProtoTimestampToLocalDateTimeConverter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.protobuf.ProtobufModule;
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
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.registerModule(new ProtobufModule());
        modelMapper.addConverter(new LocalDateTimeToProtoTimestampConverter());
        modelMapper.addConverter(new ProtoTimestampToLocalDateTimeConverter());
        return modelMapper;
    }
}
