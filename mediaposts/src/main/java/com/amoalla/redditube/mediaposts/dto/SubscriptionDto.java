package com.amoalla.redditube.mediaposts.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubscriptionDto {

    private SubscribableDto subscribable;
    private String error;

    public SubscriptionDto(String error) {
        this.error = error;
    }
}
