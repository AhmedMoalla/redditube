package com.amoalla.redditube.gateway.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@Entity
@Table(name = "redditube_user")
public class RedditubeUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Email
    @NotEmpty
    private String email;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
}


