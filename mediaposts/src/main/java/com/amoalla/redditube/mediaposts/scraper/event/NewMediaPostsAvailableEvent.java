package com.amoalla.redditube.mediaposts.scraper.event;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.mediaposts.entity.Subscribable;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class NewMediaPostsAvailableEvent extends ApplicationEvent {

    @Getter
    private final List<MediaPostDto> mediaPosts;
    
    public NewMediaPostsAvailableEvent(Subscribable subscribable, List<MediaPostDto> mediaPosts) {
        super(subscribable);
        this.mediaPosts = mediaPosts;
    }

    public Subscribable getSubscribable() {
        return (Subscribable) getSource();
    }
}
