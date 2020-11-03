package com.amoalla.redditube.commons;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Optional;

public class Utils {

    @Autowired
    private Environment environment;

    public boolean isProfileActive(String profile){
        Optional<String> result = Arrays.stream(environment.getActiveProfiles()).filter(p -> p.equalsIgnoreCase(profile)).findFirst();
        return result.isPresent();
    }
}
