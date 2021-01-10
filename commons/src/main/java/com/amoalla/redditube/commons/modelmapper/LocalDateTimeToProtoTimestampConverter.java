package com.amoalla.redditube.commons.modelmapper;

import com.google.protobuf.Timestamp;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeToProtoTimestampConverter implements Converter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convert(MappingContext<LocalDateTime, Timestamp> ctx) {
        return ctx.getSource() != null ? fromLocalDateTime(ctx.getSource()) : null;
    }

    private Timestamp fromLocalDateTime(LocalDateTime localDateTime) {
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
