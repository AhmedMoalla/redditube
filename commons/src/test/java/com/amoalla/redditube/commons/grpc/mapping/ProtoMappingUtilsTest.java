package com.amoalla.redditube.commons.grpc.mapping;

import com.amoalla.redditube.api.dto.MediaPostDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProtoMappingUtilsTest {

    @Test
    void testMapToDto() {
        MediaPostDto dto = MediaPostDto.builder()
                .id("ID")
                .mediaUrl("MEDIA_URL")
                .mediaThumbnailUrl("MEDIA_THUMBNAIL_URL")
                .username("USERNAME")
                .subreddit("SUBREDDIT")
                .title("TITLE")
                .isEmbed(false)
                .embedHtml("")
                .embedProviderName("")
                .isGallery(false)
                .galleryMediaUrls(new HashMap<>())
                .creationDateTime(LocalDateTime.now())
                .build();
        MediaPostDto mappedDto = ProtoMappingUtils.mapToDto(ProtoMappingUtils.mapToProto(dto));
        assertEquals(dto, mappedDto);
    }
}