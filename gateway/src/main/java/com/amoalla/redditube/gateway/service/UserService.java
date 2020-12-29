package com.amoalla.redditube.gateway.service;

import com.amoalla.redditube.gateway.entity.RedditubeUser;
import com.amoalla.redditube.gateway.exception.EmailAlreadyExistsException;
import com.amoalla.redditube.gateway.exception.UsernameAlreadyExistsException;
import com.amoalla.redditube.gateway.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DexService dexService;

    public UserService(UserRepository userRepository, DexService dexService) {
        this.userRepository = userRepository;
        this.dexService = dexService;
    }

    public Mono<RedditubeUser> registerUser(RedditubeUser user, String rawPassword) {
        if (userRepository.existsById(user.getUsername())) {
            throw new UsernameAlreadyExistsException(user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        RedditubeUser newUser = userRepository.save(user);
        dexService.createNewPassword(newUser, rawPassword);
        return Mono.just(newUser);
    }
}
