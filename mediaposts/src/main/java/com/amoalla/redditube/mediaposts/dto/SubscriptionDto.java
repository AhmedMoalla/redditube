package com.amoalla.redditube.mediaposts.dto;

import com.amoalla.redditube.mediaposts.entity.Subscribable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubscriptionDto {

    private Subscribable subscribable;
    private String error;

    public SubscriptionDto(Subscribable subscribable) {
        this.subscribable = subscribable;
    }

    public SubscriptionDto(String error) {
        this.error = error;
    }
}
