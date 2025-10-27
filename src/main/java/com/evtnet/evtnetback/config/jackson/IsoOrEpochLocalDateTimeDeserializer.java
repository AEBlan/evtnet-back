package com.evtnet.evtnetback.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.*;

public class IsoOrEpochLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    // 🔹 Fijamos explícitamente la zona de Argentina
    private static final ZoneId ZONA_ARG = ZoneId.of("America/Argentina/Buenos_Aires");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        if (node == null || node.isNull()) return null;

        // Número → epoch (ms o s)
        if (node.isNumber()) {
            long v = node.asLong();
            if (String.valueOf(Math.abs(v)).length() > 10) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(v), ZONA_ARG);
            } else {
                return LocalDateTime.ofInstant(Instant.ofEpochSecond(v), ZONA_ARG);
            }
        }

        // String → ISO-8601
        if (node.isTextual()) {
            String s = node.asText().trim();
            if (s.isEmpty()) return null;
            try {
                return LocalDateTime.ofInstant(OffsetDateTime.parse(s).toInstant(), ZONA_ARG);
            } catch (Exception ignore) {}
            try {
                return LocalDateTime.parse(s);
            } catch (Exception ex) {
                throw new IOException("Fecha/hora inválida: " + s, ex);
            }
        }

        throw new IOException("Tipo de fecha/hora no soportado: " + node.toString());
    }
}
