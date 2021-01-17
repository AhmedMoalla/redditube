package com.amoalla.redditube.mediaposts.scraper.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ScraperTaskSchedulerConfigurationTest {

    @Test
    void testProvidesTaskSchedulerCustomizer() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(ScraperTaskSchedulerConfiguration.class))
                .withPropertyValues("redditube.scraper.restart-period=2s")
                .run(context -> {
                    assertThat(context).hasSingleBean(TaskSchedulerCustomizer.class);
                    ScraperSchedulerProperties props = context.getBean(ScraperSchedulerProperties.class);
                    assertEquals(Duration.ofSeconds(2L), props.getRestartPeriod());
                });
    }

}