package com.amoalla.redditube.commons.grpc.impl;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.api.dto.Sort;
import com.amoalla.redditube.api.service.ExplorerService;
import com.amoalla.redditube.commons.api.Explorer;
import com.amoalla.redditube.commons.api.Explorer.GetMediaPostsRequest;
import com.amoalla.redditube.commons.api.ExplorerServiceGrpc;
import com.amoalla.redditube.commons.api.ExplorerServiceGrpc.ExplorerServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.assertj.core.util.VisibleForTesting;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.DisposableBean;
import reactor.core.publisher.Flux;

public class ExplorerServiceGrpcClientImpl implements ExplorerService, DisposableBean {

    private final ExplorerServiceBlockingStub stub;
    private final ManagedChannel channel;
    private final ModelMapper modelMapper;

    public ExplorerServiceGrpcClientImpl(String host, int grpcPort, ModelMapper modelMapper) {
        channel = ManagedChannelBuilder.forAddress(host, grpcPort)
                .usePlaintext()
                .build();
        stub = ExplorerServiceGrpc.newBlockingStub(channel);
        this.modelMapper = modelMapper;
    }

    @VisibleForTesting
    ExplorerServiceGrpcClientImpl(ManagedChannel channel, ModelMapper modelMapper) {
        this.channel = channel;
        stub = ExplorerServiceGrpc.newBlockingStub(channel);
        this.modelMapper = modelMapper;
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
                .map(proto -> modelMapper.map(proto, MediaPostDto.class));
    }

    @Override
    public void destroy() {
        channel.shutdownNow();
    }
}
