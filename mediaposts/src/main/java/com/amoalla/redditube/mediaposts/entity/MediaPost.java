package com.amoalla.redditube.mediaposts.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "media_post")
public class MediaPost {

    @Id
    private String id;
    @NotNull
    private String title;
    @NotNull
    private String objectId;
    @NotNull
    private String thumbnailObjectId;
    @NotNull
    private Boolean isVideo = false;
    @NotNull
    private LocalDateTime creationDateTime;
    @NotNull
    @ManyToOne
    private Subscribable owner;
}
