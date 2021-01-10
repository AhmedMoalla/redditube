package com.amoalla.redditube.commons.modelmapper;

import com.google.protobuf.Timestamp;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ProtoTimestampToLocalDateTimeConverter implements Converter<Timestamp, LocalDateTime> {

    @Override
    public LocalDateTime convert(MappingContext<Timestamp, LocalDateTime> ctx) {
        return ctx.getSource() != null ? fromProtoTimestamp(ctx.getSource()) : null;
    }

    private LocalDateTime fromProtoTimestamp(Timestamp timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
    }
}
