package com.amoalla.redditube.mediaposts.scraper.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class MediaPostsScraperTaskFinished extends ApplicationEvent {

    @Getter
    private final int totalNbTasks;

    public MediaPostsScraperTaskFinished(int totalNbTasks) {
        super(new Object());
        this.totalNbTasks = totalNbTasks;
    }
}
