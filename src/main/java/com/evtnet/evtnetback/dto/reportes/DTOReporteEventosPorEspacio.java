package com.evtnet.evtnetback.dto.reportes;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DTOReporteEventosPorEspacio {
    private Instant fechaHoraGeneracion;
    private List<Dato> datos;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Dato {
        private String espacio;        // nombre del espacio
        private Instant fechaDesde;    // rango solicitado (no del evento)
        private Instant fechaHasta;    // rango solicitado (no del evento)
        private long eventos;          // cantidad de eventos del espacio en el rango
    }
}
