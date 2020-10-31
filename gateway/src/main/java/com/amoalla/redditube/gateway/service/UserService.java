package com.amoalla.redditube.gateway.service;

import com.amoalla.redditube.gateway.dto.dex.PasswordVerification;
import com.amoalla.redditube.gateway.entity.RedditubeUser;
import com.amoalla.redditube.gateway.exception.UserAlreadyExistsException;
import com.amoalla.redditube.gateway.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DexService dexService;

    public UserService(UserRepository userRepository, DexService dexService) {
        this.userRepository = userRepository;
        this.dexService = dexService;
    }

    public Mono<RedditubeUser> registerUser(RedditubeUser user, String rawPassword) throws UserAlreadyExistsException {
        Optional<RedditubeUser> foundUserOpt = userRepository.findByEmail(user.getEmail());
        if (foundUserOpt.isPresent()) {
            RedditubeUser foundUser = foundUserOpt.get();
            PasswordVerification verification = dexService.verifyPassword(user.getEmail(), rawPassword);
            if (verification.isNotFound()) {
                dexService.createNewPassword(foundUser, rawPassword);
                return Mono.just(foundUser);
            }
            throw new UserAlreadyExistsException(user.getEmail());
        }
        RedditubeUser newUser = userRepository.save(user);
        dexService.createNewPassword(newUser, rawPassword);
        return Mono.just(newUser);
    }
}
