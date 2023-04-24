package com.onemsg.restdatafaker;

import java.time.LocalDateTime;

import io.vertx.core.json.JsonObject;
import lombok.Builder;

/**
 * Info of rest data faker
 */
@Builder
public record FakerInfo (
    int id,
    String path,
    String name,
    String description,
    JsonObject expression,
    String locale,
    FakerType type,
    Delay delay,
    LocalDateTime createdTime,
    LocalDateTime updatedTime
) {
    
    public static FakerInfoBuilder builder(FakerInfo other) {
        return new FakerInfoBuilder()
            .id(other.id())
            .path(other.path())
            .name(other.name())
            .description(other.description())
            .expression(other.expression())
            .locale(other.locale())
            .type(other.type())
            .delay(other.delay())
            .createdTime(other.createdTime())
            .updatedTime(other.updatedTime());
    }
}
