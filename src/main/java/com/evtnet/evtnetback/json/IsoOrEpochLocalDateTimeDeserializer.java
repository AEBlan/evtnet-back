package com.evtnet.evtnetback.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.*;

public class IsoOrEpochLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node == null || node.isNull()) return null;

        ZoneId zone = ZoneId.systemDefault();

        // Número → epoch (ms o s)
        if (node.isNumber()) {
            long v = node.asLong();
            // heurística: si tiene más de 10 dígitos -> milisegundos
            if (String.valueOf(Math.abs(v)).length() > 10) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(v), zone);
            } else {
                return LocalDateTime.ofInstant(Instant.ofEpochSecond(v), zone);
            }
        }

        // String → ISO-8601 (con o sin offset)
        if (node.isTextual()) {
            String s = node.asText().trim();
            if (s.isEmpty()) return null;
            try {
                // con offset, ej: 2025-09-17T21:00:00-03:00
                return LocalDateTime.ofInstant(OffsetDateTime.parse(s).toInstant(), zone);
            } catch (Exception ignore) {}
            try {
                // sin offset, ej: 2025-09-17T21:00:00 (interpretar como hora local)
                return LocalDateTime.parse(s);
            } catch (Exception ex) {
                throw new IOException("Fecha/hora inválida: " + s, ex);
            }
        }

        throw new IOException("Tipo de fecha/hora no soportado: " + node.toString());
    }
}
