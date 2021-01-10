package com.amoalla.redditube.explorer.grpc;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.dto.Sort;
import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.commons.api.Explorer;
import com.amoalla.redditube.commons.api.Explorer.GetMediaPostsRequest;
import com.amoalla.redditube.commons.api.Explorer.GetMediaPostsResponse;
import com.amoalla.redditube.commons.api.Explorer.MediaPost;
import com.amoalla.redditube.commons.api.ExplorerServiceGrpc.ExplorerServiceImplBase;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Flux;

@Slf4j
@GRpcService
public class ExplorerGrpcServiceImpl extends ExplorerServiceImplBase {

    private final ExplorerService explorerService;
    private final ModelMapper modelMapper;

    public ExplorerGrpcServiceImpl(ExplorerService explorerService, ModelMapper modelMapper) {
        this.explorerService = explorerService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void getMediaPosts(GetMediaPostsRequest params, StreamObserver<GetMediaPostsResponse> responseObserver) {
        final String usernameOrSubreddit = params.getUsernameOrSubreddit();
        final String after = params.getAfter();
        final String before = params.getBefore();
        final int limit = params.getLimit();
        final Flux<MediaPostDto> mediaPosts;

        log.info("Received getMediaPosts(usernameOrSubreddit={}, after={}, before={}, limit={}, sort={})",
                usernameOrSubreddit, after, before, limit, params.getSort().name());

        if (Explorer.Sort.UNRECOGNIZED.equals(params.getSort())) {
            mediaPosts = explorerService.getPosts(usernameOrSubreddit, after, before, limit);
        } else {
            Sort sort = Sort.valueOf(params.getSort().name());
            mediaPosts = explorerService.getPosts(usernameOrSubreddit, after, before, limit, sort);
        }

        Iterable<MediaPost> posts = mediaPosts.map(dto -> modelMapper.map(dto, MediaPost.Builder.class).build()).toIterable();
        GetMediaPostsResponse response = GetMediaPostsResponse.newBuilder()
                .addAllMediaPosts(posts)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
