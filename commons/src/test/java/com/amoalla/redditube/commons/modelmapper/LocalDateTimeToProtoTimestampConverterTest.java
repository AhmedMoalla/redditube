package com.amoalla.redditube.commons.modelmapper;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.spi.MappingContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class LocalDateTimeToProtoTimestampConverterTest {

    @Test
    @SuppressWarnings("unchecked")
    void testConvert() {
        var converter = new LocalDateTimeToProtoTimestampConverter();
        LocalDateTime now = LocalDateTime.now();
        MappingContext<LocalDateTime, Timestamp> ctx = Mockito.mock(MappingContext.class);
        Mockito.when(ctx.getSource()).thenReturn(now);
        Timestamp timestamp = converter.convert(ctx);
        Instant nowInstant = now.toInstant(ZoneOffset.UTC);
        Timestamp expected = Timestamp.newBuilder()
                .setSeconds(nowInstant.getEpochSecond())
                .setNanos(nowInstant.getNano())
                .build();
        assertEquals(expected, timestamp);
    }
}