package com.evtnet.evtnetback.util;

import java.time.*;

public final class TimeUtil {
    private static final ZoneId ZONE = ZoneId.of("America/Argentina/Salta"); // cambia si querés
    private TimeUtil(){}

    public static LocalDateTime fromMillis(Long ms) {
        if (ms == null) return null;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZONE);
    }

    public static Long toMillis(LocalDateTime ldt) {
        if (ldt == null) return null;
        return ldt.atZone(ZONE).toInstant().toEpochMilli();
    }
}
