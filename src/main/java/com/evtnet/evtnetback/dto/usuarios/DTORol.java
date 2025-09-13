package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTORol {
    private Long id;
    private String nombre;
    private String descripcion;
    private boolean reservado;
    private LocalDateTime fechaAlta;
    private LocalDateTime fechaBaja;
    private List<PermisoEnRol> permisos;

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PermisoEnRol {
        private String nombre;
        private boolean reservado;
        private List<Periodo> periodos;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Periodo {
        private LocalDateTime desde;
        private LocalDateTime hasta; // puede ser null (vigente)
    }
}
