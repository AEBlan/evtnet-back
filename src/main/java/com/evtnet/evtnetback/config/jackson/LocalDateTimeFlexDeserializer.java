package com.evtnet.evtnetback.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeParseException;

public class LocalDateTimeFlexDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();

        if (t == JsonToken.VALUE_NUMBER_INT) {
            long ms = p.getLongValue();
            return Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        String raw = p.getValueAsString();
        if (raw == null || raw.isBlank()) return null;

        // Handle numeric strings
        if (raw.chars().allMatch(Character::isDigit)) {
            long ms = Long.parseLong(raw);
            return Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }

        // Try ISO LocalDateTime format
        try {
            return LocalDateTime.parse(raw);
        } catch (DateTimeParseException ignore) {}

        // Try ISO Instant format
        try {
            return Instant.parse(raw).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (DateTimeParseException ignore) {}

        throw new InvalidFormatException(p,
                "Expected milliseconds or ISO-8601 format", raw, LocalDateTime.class);
    }
}