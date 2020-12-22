package com.amoalla.redditube.client.model;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class MediaPostListings {
    @Getter
    private final List<MediaPostDto> mediaPosts = new ArrayList<>();

    @JacksonInject
    private ObjectMapper objectMapper;

    @JsonProperty("data")
    private void setMediaPosts(JsonNode data) {
        JsonNode children = data.get("children");
        StreamSupport.stream(children.spliterator(), false)
                .map(node -> node.get("data"))
                .map(this::toMediaPost)
                .forEach(mediaPosts::add);
    }

    @SneakyThrows
    public MediaPostDto toMediaPost(JsonNode node) {
        return objectMapper.treeToValue(node, MediaPostDto.class);
    }
}