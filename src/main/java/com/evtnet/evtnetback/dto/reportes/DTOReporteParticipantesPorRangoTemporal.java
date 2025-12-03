package com.evtnet.evtnetback.dto.reportes;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Data
public class DTOReporteParticipantesPorRangoTemporal {

    private Instant fechaHoraGeneracion;
    private List<Entrada> datos;

    @Data
    public static class Entrada {
        private String espacio;
        private List<Rango> rangos;
    }

    @Data
    public static class Rango {
        private long inicio;       // epoch millis
        private long fin;          // epoch millis
        private long participantes;
    }
}
