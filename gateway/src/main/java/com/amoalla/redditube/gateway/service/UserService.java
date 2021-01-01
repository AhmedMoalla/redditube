package com.amoalla.redditube.gateway.service;

import com.amoalla.redditube.gateway.entity.RedditubeUser;
import com.amoalla.redditube.gateway.exception.EmailAlreadyExistsException;
import com.amoalla.redditube.gateway.exception.UsernameAlreadyExistsException;
import com.amoalla.redditube.gateway.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final IdpService idpService;

    public UserService(UserRepository userRepository, IdpService idpService) {
        this.userRepository = userRepository;
        this.idpService = idpService;
    }

    @Transactional
    public Mono<RedditubeUser> registerUser(RedditubeUser user, String rawPassword) {
        if (userRepository.existsById(user.getUsername())) {
            throw new UsernameAlreadyExistsException(user.getUsername());
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException(user.getEmail());
        }

        idpService.registerUser(user, rawPassword);
        return Mono.just(userRepository.save(user));
    }
}
