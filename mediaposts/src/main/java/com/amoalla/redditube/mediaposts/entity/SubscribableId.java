package com.amoalla.redditube.mediaposts.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribableId implements Serializable {
    private String id;
    private SubscribableType type;
}
