package com.amoalla.redditube.gateway.service;

import com.amoalla.redditube.gateway.configuration.properties.DexProperties;
import com.amoalla.redditube.gateway.dto.dex.PasswordVerification;
import com.amoalla.redditube.gateway.entity.RedditubeUser;
import com.amoalla.redditube.gateway.exception.UsernameAlreadyExistsException;
import com.coreos.dex.api.DexApi;
import com.coreos.dex.api.DexApi.CreatePasswordReq;
import com.coreos.dex.api.DexApi.Password;
import com.coreos.dex.api.DexGrpc;
import com.coreos.dex.api.DexGrpc.DexBlockingStub;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DexService implements InitializingBean {

    private final DexProperties properties;
    private final PasswordEncoder passwordEncoder;

    private DexBlockingStub dexStub;

    public DexService(DexProperties properties, PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void afterPropertiesSet() {
        setDexStub(createBlockingStub());
    }

    public void createNewPassword(RedditubeUser user, String rawPassword) {
        String hash = passwordEncoder.encode(rawPassword);
        ByteString hashBytes = ByteString.copyFrom(hash.getBytes());
        var password = Password.newBuilder()
                .setEmail(user.getEmail())
                .setUsername(user.getUsername())
                .setUserId(user.getUsername())
                .setHash(hashBytes)
                .build();
        var createPassReq = CreatePasswordReq.newBuilder()
                .setPassword(password)
                .build();
        var createPassRes = dexStub.createPassword(createPassReq);
        if (createPassRes.getAlreadyExists()) {
            throw new UsernameAlreadyExistsException(user.getEmail());
        }
        log.info("Password created successfully on Dex for user: {}", user.getEmail());
    }

    public PasswordVerification verifyPassword(String email, String password) {
        var verifyPasswordReq = DexApi.VerifyPasswordReq.newBuilder()
                .setEmail(email)
                .setPassword(password)
                .build();

        var verifyPasswordRes = dexStub.verifyPassword(verifyPasswordReq);
        return new PasswordVerification(verifyPasswordRes.getVerified(), verifyPasswordRes.getNotFound());
    }

    private DexBlockingStub createBlockingStub() {
        var channel = ManagedChannelBuilder.forAddress(properties.getHost(), properties.getPort())
                .usePlaintext()
                .build();
        return DexGrpc.newBlockingStub(channel);
    }

    public void setDexStub(DexBlockingStub dexStub) {
        this.dexStub = dexStub;
    }
}
