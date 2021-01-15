package com.amoalla.redditube.mediaposts.scraper.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties("redditube.scraper")
public class ScraperSchedulerProperties {
    /**
     * Period after which the next scraping task is executed after the current one has finished
     */
    private Duration restartPeriod;
}
