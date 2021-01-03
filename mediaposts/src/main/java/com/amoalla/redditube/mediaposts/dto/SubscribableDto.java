package com.amoalla.redditube.mediaposts.dto;

import com.amoalla.redditube.mediaposts.entity.SubscribableType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SubscribableDto {

    @NotNull
    private String handle;
    @NotNull
    private SubscribableType type;
}
