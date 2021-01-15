package com.amoalla.redditube.mediaposts.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * An entity to be subscribed to. Represents a User or a Subreddit
 */
@Data
@Entity
@Table(name = "subscribable")
public class Subscribable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String handle;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SubscribableType type;

    private String lastFetchedPostId;

    private Integer lastFetchedPostCount;

    @OneToMany(mappedBy = "subscribable")
    private List<Subscription> subscriptions;

    @OneToMany(mappedBy = "owner")
    private List<MediaPost> mediaPosts;
}
