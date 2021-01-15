package com.amoalla.redditube.commons.grpc.impl;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.dto.Sort;
import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.commons.api.Explorer;
import com.amoalla.redditube.commons.api.Explorer.GetMediaPostsRequest;
import com.amoalla.redditube.commons.api.ExplorerServiceGrpc;
import com.amoalla.redditube.commons.api.ExplorerServiceGrpc.ExplorerServiceBlockingStub;
import com.amoalla.redditube.commons.grpc.mapping.ProtoMappingUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.assertj.core.util.VisibleForTesting;
import org.springframework.beans.factory.DisposableBean;
import reactor.core.publisher.Flux;

public class ExplorerServiceGrpcClientImpl implements ExplorerService, DisposableBean {

    private final ExplorerServiceBlockingStub stub;
    private final ManagedChannel channel;

    public ExplorerServiceGrpcClientImpl(String host, int grpcPort) {
        channel = ManagedChannelBuilder.forAddress(host, grpcPort)
                .usePlaintext()
                .build();
        stub = ExplorerServiceGrpc.newBlockingStub(channel);
    }

    @VisibleForTesting
    ExplorerServiceGrpcClientImpl(ManagedChannel channel) {
        this.channel = channel;
        stub = ExplorerServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public Flux<MediaPostDto> getPosts(String usernameOrSubreddit, String after, String before, int limit) {
        return getPosts(usernameOrSubreddit, after, before, limit, null);
    }

    @Override
    public Flux<MediaPostDto> getPosts(String usernameOrSubreddit, String after, String before, int limit, Sort sort) {
        GetMediaPostsRequest.Builder request = GetMediaPostsRequest.newBuilder()
                .setUsernameOrSubreddit(usernameOrSubreddit)
                .setAfter(after)
                .setBefore(before)
                .setLimit(limit);

        if (sort != null) {
            request.setSort(Explorer.Sort.valueOf(sort.name()));
        }

        return Flux.fromIterable(stub.getMediaPosts(request.build()).getMediaPostsList())
                .map(ProtoMappingUtils::mapToDto);
    }

    @Override
    public void destroy() {
        channel.shutdownNow();
    }
}
