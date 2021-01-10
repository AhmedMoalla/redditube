package com.amoalla.redditube.mediaposts.scraper.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@Configuration
@EnableScheduling
@EnableConfigurationProperties(ScraperSchedulerProperties.class)
public class ScraperTaskSchedulerConfiguration {

    @Bean
    public TaskSchedulerCustomizer taskSchedulerCustomizer() {
        return scheduler -> scheduler.setErrorHandler(
                ex -> log.error("Error happened within scheduler. An error event should be emitter.", ex));
    }
}
