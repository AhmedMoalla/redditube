package com.amoalla.redditube.mediaposts.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SubscriptionID implements Serializable {
    private String username;
    private Subscribable subscribable;
}
