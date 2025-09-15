// DTOEventosUsuario.java
package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DTOEventosUsuario {
    private List<Organizador> organizador;
    private List<Administrador> administrador;
    private List<Participante> participante;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Organizador {
        private Long id;
        private String nombre;
        private LocalDateTime fechaDesde; // = Evento.fechaHoraInicio
        private LocalDateTime fechaHasta; // = Evento.fechaHoraFin
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Participante {
        private Long id;
        private String nombre;
        private LocalDateTime fechaDesde;
        private LocalDateTime fechaHasta;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Administrador {
        private Long id;
        private String nombre;
        private LocalDateTime fechaDesde; // del evento
        private LocalDateTime fechaHasta; // del evento
        private List<Periodo> periodos;   // alta/baja de la administración
    }

    @Data @AllArgsConstructor
    public static class Periodo {
        private LocalDateTime desde;
        private LocalDateTime hasta;
    }
}
