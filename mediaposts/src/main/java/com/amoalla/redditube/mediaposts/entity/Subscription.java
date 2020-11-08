package com.amoalla.redditube.mediaposts.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "subscription")
public class Subscription {

    @Id
    private Integer id;

    @NotNull
    @Column(name = "user_id")
    private String userId;

    @NotNull
    @OneToOne
    private Subscribable subscribable;
}
