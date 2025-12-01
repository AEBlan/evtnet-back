package com.evtnet.evtnetback.dto.reportes;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOReporteRegistracionesIniciosSesion {
    private Instant fechaHoraGeneracion;
    private List<Dato> datos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dato {
        private Instant inicio;
        private Instant fin;
        private long registraciones;
        private long iniciosSesion;
        private double proporcion;
    }
}
