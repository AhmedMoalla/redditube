package com.amoalla.redditube.gateway.service;

import com.amoalla.redditube.gateway.entity.RedditubeUser;
import com.coreos.dex.api.DexApi;
import com.coreos.dex.api.DexApi.CreatePasswordReq;
import com.coreos.dex.api.DexApi.CreatePasswordResp;
import com.coreos.dex.api.DexApi.VerifyPasswordReq;
import com.coreos.dex.api.DexApi.VerifyPasswordResp;
import com.coreos.dex.api.DexGrpc;
import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class DexServiceTest {

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_EMAIL = "TEST_EMAIL";
    private static final String TEST_PASSWORD = "TEST_PASSWORD";

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private DexGrpc.DexImplBase serviceImpl;
    private DexService dexService;

    @BeforeEach
    void setUp() throws IOException {

        serviceImpl = mock(DexGrpc.DexImplBase.class, delegatesTo(
                new DexGrpc.DexImplBase() {
                    @Override
                    public void createPassword(CreatePasswordReq request, StreamObserver<CreatePasswordResp> responseObserver) {
                        responseObserver.onNext(CreatePasswordResp.getDefaultInstance());
                        responseObserver.onCompleted();
                    }

                    @Override
                    public void verifyPassword(VerifyPasswordReq request, StreamObserver<VerifyPasswordResp> responseObserver) {
                        responseObserver.onNext(VerifyPasswordResp.getDefaultInstance());
                        responseObserver.onCompleted();
                    }
                }
        ));

        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(serviceImpl).build().start());

        // Create a client channel and register for automatic graceful shutdown.
        ManagedChannel channel = grpcCleanup.register(
                InProcessChannelBuilder.forName(serverName).directExecutor().build());

        dexService = new DexService(null, passwordEncoder);
        dexService.setDexStub(DexGrpc.newBlockingStub(channel));
    }

    @Test
    void testCreateNewPassword() {
        RedditubeUser user = new RedditubeUser();
        user.setEmail(TEST_EMAIL);
        user.setUsername(TEST_USERNAME);

        ArgumentCaptor<CreatePasswordReq> requestCaptor = ArgumentCaptor.forClass(CreatePasswordReq.class);

        dexService.createNewPassword(user, TEST_PASSWORD);

        verify(serviceImpl)
                .createPassword(requestCaptor.capture(), ArgumentMatchers.any());
        DexApi.Password passwordSent = requestCaptor.getValue().getPassword();
        String sentHash = passwordSent.getHash().toStringUtf8();
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, sentHash));
        assertEquals(TEST_EMAIL, passwordSent.getEmail());
        assertEquals(TEST_USERNAME, passwordSent.getUsername());
        assertEquals(TEST_USERNAME, passwordSent.getUsername());
    }

    @Test
    void testVerifyPassword() {
        ArgumentCaptor<VerifyPasswordReq> requestCaptor = ArgumentCaptor.forClass(VerifyPasswordReq.class);

        dexService.verifyPassword(TEST_EMAIL, TEST_PASSWORD);

        verify(serviceImpl)
                .verifyPassword(requestCaptor.capture(), ArgumentMatchers.any());
        VerifyPasswordReq sentReq = requestCaptor.getValue();
        assertEquals(TEST_EMAIL, sentReq.getEmail());
        assertEquals(TEST_PASSWORD, sentReq.getPassword());
    }

}