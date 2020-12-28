package com.amoalla.redditube.mediaposts.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "subscription")
@IdClass(SubscriptionId.class)
public class Subscription {

    @Id
    @NotNull
    @Column(name = "username")
    private String username;

    @Id
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscribable_id")
    @JoinColumn(name = "subscribable_type")
    private Subscribable subscribable;
}
