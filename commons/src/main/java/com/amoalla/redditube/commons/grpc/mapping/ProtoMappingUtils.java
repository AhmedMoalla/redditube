package com.amoalla.redditube.commons.grpc.mapping;

import com.amoalla.redditube.api.dto.MediaPostDto;
import com.amoalla.redditube.commons.api.Explorer;
import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;

public class ProtoMappingUtils {

    private ProtoMappingUtils() {}

    public static MediaPostDto mapToDto(Explorer.MediaPost protoMediaPost) {
        return MediaPostDto.builder()
                .id(protoMediaPost.getId())
                .mediaUrl(protoMediaPost.getMediaUrl())
                .mediaThumbnailUrl(protoMediaPost.getMediaThumbnailUrl())
                .username(protoMediaPost.getUsername())
                .subreddit(protoMediaPost.getSubreddit())
                .title(protoMediaPost.getTitle())
                .isEmbed(protoMediaPost.getIsEmbed())
                .embedHtml(protoMediaPost.getEmbedHtml())
                .embedProviderName(protoMediaPost.getEmbedProviderName())
                .isGallery(protoMediaPost.getIsGallery())
                .galleryMediaUrls(protoMediaPost.getGalleryMediaUrlsMap())
                .creationDateTime(fromProtoTimestamp(protoMediaPost.getCreationDateTime()))
                .build();
    }

    public static Explorer.MediaPost mapToProto(MediaPostDto dto) {
        return Explorer.MediaPost.newBuilder()
                .setId(dto.getId())
                .setMediaUrl(dto.getMediaUrl())
                .setMediaThumbnailUrl(dto.getMediaThumbnailUrl())
                .setUsername(dto.getUsername())
                .setSubreddit(dto.getSubreddit())
                .setTitle(dto.getTitle())
                .setIsEmbed(dto.isEmbed())
                .setEmbedHtml(Objects.requireNonNullElse(dto.getEmbedHtml(), ""))
                .setEmbedProviderName(Objects.requireNonNullElse(dto.getEmbedProviderName(), ""))
                .setCreationDateTime(fromLocalDateTime(dto.getCreationDateTime()))
                .setIsGallery(dto.isGallery())
                .putAllGalleryMediaUrls(dto.getGalleryMediaUrls())
                .build();
    }

    private static LocalDateTime fromProtoTimestamp(Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }

    private static Timestamp fromLocalDateTime(LocalDateTime localDateTime) {
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
