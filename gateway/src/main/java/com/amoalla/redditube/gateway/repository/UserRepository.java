package com.amoalla.redditube.gateway.repository;

import com.amoalla.redditube.gateway.entity.RedditubeUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Repository
public interface UserRepository extends CrudRepository<RedditubeUser, String> {
    boolean existsByEmail(@Email @NotEmpty String email);
}
