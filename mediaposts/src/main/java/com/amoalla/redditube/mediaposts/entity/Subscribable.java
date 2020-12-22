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
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SubscribableType type;

    @OneToMany
    private List<Subscription> subscriptions;
}
