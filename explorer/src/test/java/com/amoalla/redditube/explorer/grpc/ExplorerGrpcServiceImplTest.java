package com.amoalla.redditube.explorer.grpc;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.dto.Sort;
import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.commons.CommonsConfiguration;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Flux;

import java.util.Collections;

import static com.amoalla.redditube.commons.api.Explorer.*;
import static org.mockito.Mockito.*;

class ExplorerGrpcServiceImplTest {

    public static final String TEST_ID = "TEST_ID";
    public static final String TEST_MEDIA_URL = "TEST_MEDIA_URL";
    public static final String TEST_HANDLE = "TEST_HANDLE";
    public static final String TEST_AFTER = "TEST_AFTER";
    public static final String TEST_BEFORE = "TEST_BEFORE";
    public static final int TEST_LIMIT = 10;

    private ExplorerGrpcServiceImpl explorerGrpcService;
    private MediaPostDto testDto;

    @BeforeEach
    void setUp() {
        ExplorerService explorerService = mock(ExplorerService.class);
        testDto = new MediaPostDto();
        testDto.setId(TEST_ID);
        testDto.setMediaUrl(TEST_MEDIA_URL);
        when(explorerService.getPosts(TEST_HANDLE, TEST_AFTER, TEST_BEFORE, TEST_LIMIT, Sort.NEW))
                .thenReturn(Flux.just(testDto));
        ModelMapper modelMapper = new CommonsConfiguration().provideModelMapper();
        explorerGrpcService = new ExplorerGrpcServiceImpl(explorerService, modelMapper);
    }

    @Test
    void testGetMediaPosts() {
        GetMediaPostsRequest request = GetMediaPostsRequest.newBuilder()
                .setUsernameOrSubreddit(TEST_HANDLE)
                .setAfter(TEST_AFTER)
                .setBefore(TEST_BEFORE)
                .setLimit(TEST_LIMIT)
                .build();
        StreamObserver<GetMediaPostsResponse> observer = mock(StreamObserver.class);
        explorerGrpcService.getMediaPosts(request, observer);

        MediaPost expected = MediaPost.newBuilder()
                .setId(TEST_ID)
                .setMediaUrl(TEST_MEDIA_URL)
                .build();
        GetMediaPostsResponse expectedResponse = GetMediaPostsResponse.newBuilder()
                .addAllMediaPosts(Collections.singletonList(expected))
                .build();
        verify(observer).onNext(expectedResponse);
        verify(observer).onCompleted();
    }
}