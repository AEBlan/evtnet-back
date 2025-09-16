package com.evtnet.evtnetback.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeParseException;

public class LocalDateFlexDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken t = p.getCurrentToken();

        if (t == JsonToken.VALUE_NUMBER_INT) {
            // número -> interpretamos como MILISEGUNDOS
            long ms = p.getLongValue();
            return Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate();
        }

        String raw = p.getValueAsString();
        if (raw == null || raw.isBlank()) return null;

        // ¿string numérica? también tratamos como ms
        boolean soloDigitos = raw.chars().allMatch(Character::isDigit);
        if (soloDigitos) {
            long ms = Long.parseLong(raw);
            return Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDate();
        }

        // Intento ISO "yyyy-MM-dd"
        try {
            return LocalDate.parse(raw);
        } catch (DateTimeParseException ignore) {
        }

        // Intento ISO instant "yyyy-MM-dd'T'HH:mm:ss[.SSS]X"
        try {
            return Instant.parse(raw).atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (DateTimeParseException ignore) {
        }

        throw new InvalidFormatException(p,
                "fechaNacimiento debe ser milisegundos o 'yyyy-MM-dd' o ISO-8601", raw, LocalDate.class);
    }
}
