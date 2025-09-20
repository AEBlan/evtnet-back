package com.evtnet.evtnetback.dto.reportes;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DTOReportePersonsasEnEventosEnEspacio {
    private Instant fechaHoraGeneracion;
    private List<Dato> datos;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Dato {
        private String evento;        // nombre del evento
        private Instant fechaDesde;   // inicio del evento
        private Instant fechaHasta;   // fin del evento
        private long participantes;   // cantidad de inscriptos (ver nota abajo)
    }
}