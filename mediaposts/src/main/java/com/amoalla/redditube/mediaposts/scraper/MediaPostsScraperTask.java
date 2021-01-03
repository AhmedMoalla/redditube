package com.amoalla.redditube.mediaposts.scraper;

import com.amoalla.redditube.mediaposts.entity.Subscribable;
import com.amoalla.redditube.mediaposts.repository.SubscribableRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
public class MediaPostsScraperTask implements Runnable, InitializingBean {

    private final ThreadPoolTaskScheduler scheduler;
    private final SubscribableRepository repository;

    public MediaPostsScraperTask(ThreadPoolTaskScheduler scheduler, SubscribableRepository repository) {
        this.scheduler = scheduler;
        this.repository = repository;
    }

    @Override
    public void afterPropertiesSet() {
        scheduler.scheduleAtFixedRate(this, Duration.of(30, ChronoUnit.SECONDS));
    }

    @Override
    public void run() {
        Iterable<Subscribable> subscribables = repository.findAll();
        
    }
}
