package com.evtnet.evtnetback.dto.reportes;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class DTOReporteParticipantesPorRangoTemporal {
    private Instant fechaHoraGeneracion;
    private List<Dato> datos;

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class Dato {
        private String espacio;
        private Instant fechaDesde;
        private Instant fechaHasta;
        private long participantes;
    }
}
