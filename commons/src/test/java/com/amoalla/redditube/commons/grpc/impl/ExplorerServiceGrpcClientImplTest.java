package com.amoalla.redditube.commons.grpc.impl;

import com.amoalla.redditube.api.dto.Sort;
import com.amoalla.redditube.commons.api.Explorer.GetMediaPostsRequest;
import com.amoalla.redditube.commons.api.Explorer.GetMediaPostsResponse;
import com.amoalla.redditube.commons.api.ExplorerServiceGrpc.ExplorerServiceImplBase;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.delegatesTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ExplorerServiceGrpcClientImplTest {

    private static final int TEST_LIMIT = 20;
    private static final String TEST_AFTER = "TEST_AFTER";
    private static final String TEST_BEFORE = "TEST_BEFORE";
    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final Sort TEST_SORT = Sort.NEW;

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    private ExplorerServiceImplBase serviceImpl;
    private ExplorerServiceGrpcClientImpl explorerService;

    @BeforeEach
    void setUp() throws IOException {

        serviceImpl = mock(ExplorerServiceImplBase.class, delegatesTo(
                new ExplorerServiceImplBase() {
                    @Override
                    public void getMediaPosts(GetMediaPostsRequest request, StreamObserver<GetMediaPostsResponse> responseObserver) {
                        responseObserver.onNext(GetMediaPostsResponse.getDefaultInstance());
                        responseObserver.onCompleted();
                    }
                }
        ));

        // Create in process server
        String serverName = InProcessServerBuilder.generateName();
        Server inProcessServer = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(serviceImpl)
                .build()
                .start();
        grpcCleanup.register(inProcessServer);

        // Create a channel
        ManagedChannel channel = InProcessChannelBuilder.forName(serverName)
                .directExecutor()
                .build();
        grpcCleanup.register(channel);

        // Create Service
        explorerService = new ExplorerServiceGrpcClientImpl(channel);
    }

    @AfterEach
    void tearDown() {
        explorerService.destroy();
    }

    @Test
    void testGetPosts() {

        ArgumentCaptor<GetMediaPostsRequest> requestCaptor = ArgumentCaptor.forClass(GetMediaPostsRequest.class);

        explorerService.getPosts(TEST_USERNAME, TEST_AFTER, TEST_BEFORE, TEST_LIMIT);

        verify(serviceImpl).getMediaPosts(requestCaptor.capture(), ArgumentMatchers.any());
        GetMediaPostsRequest request = requestCaptor.getValue();
        assertEquals(TEST_USERNAME, request.getUsernameOrSubreddit());
        assertEquals(TEST_AFTER, request.getAfter());
        assertEquals(TEST_BEFORE, request.getBefore());
        assertEquals(TEST_LIMIT, request.getLimit());
    }

    @Test
    void testGetPostsWithSorting() {

        ArgumentCaptor<GetMediaPostsRequest> requestCaptor = ArgumentCaptor.forClass(GetMediaPostsRequest.class);

        explorerService.getPosts(TEST_USERNAME, TEST_AFTER, TEST_BEFORE, TEST_LIMIT, TEST_SORT);

        verify(serviceImpl).getMediaPosts(requestCaptor.capture(), ArgumentMatchers.any());
        GetMediaPostsRequest request = requestCaptor.getValue();
        assertEquals(TEST_USERNAME, request.getUsernameOrSubreddit());
        assertEquals(TEST_AFTER, request.getAfter());
        assertEquals(TEST_BEFORE, request.getBefore());
        assertEquals(TEST_LIMIT, request.getLimit());
        assertEquals(TEST_SORT, Sort.valueOf(request.getSort().name()));
    }

}