package com.amoalla.redditube.mediaposts.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "subscription")
@IdClass(SubscriptionID.class)
public class Subscription {

    @Id
    @NotNull
    @Column(name = "username")
    private String username;

    @Id
    @NotNull
    @ManyToOne
    private Subscribable subscribable;
}
