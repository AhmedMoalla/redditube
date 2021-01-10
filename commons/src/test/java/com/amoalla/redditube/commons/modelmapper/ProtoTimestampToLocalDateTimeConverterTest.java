package com.amoalla.redditube.commons.modelmapper;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.spi.MappingContext;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class ProtoTimestampToLocalDateTimeConverterTest {

    @Test
    @SuppressWarnings("unchecked")
    void testConvert() {
        var converter = new ProtoTimestampToLocalDateTimeConverter();
        Instant now = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Timestamp source = Timestamp.newBuilder()
                .setSeconds(now.getEpochSecond())
                .setNanos(now.getNano())
                .build();
        MappingContext<Timestamp, LocalDateTime> ctx = Mockito.mock(MappingContext.class);
        Mockito.when(ctx.getSource()).thenReturn(source);
        LocalDateTime localDateTime = converter.convert(ctx);
        LocalDateTime expected = LocalDateTime.ofInstant(now, ZoneId.of("UTC"));
        assertEquals(expected, localDateTime);
    }
}