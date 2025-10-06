// DTOSupereventosUsuario.java
package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DTOSupereventosUsuario {
    private List<Organizador> organizador;
    private List<Administrador> administrador;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Organizador {
        private Long id;
        private String nombre;
        private LocalDateTime fechaDesde; // min(fechaHoraInicio) de sus eventos
        private LocalDateTime fechaHasta; // max(fechaHoraFin) de sus eventos
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Administrador {
        private Long id;
        private String nombre;
        private LocalDateTime fechaDesde; // idem arriba
        private LocalDateTime fechaHasta; // idem arriba
        private List<Periodo> periodos;   // alta/baja admin super
    }

    @Data @AllArgsConstructor
    public static class Periodo {
        private LocalDateTime desde;
        private LocalDateTime hasta;
    }
}
