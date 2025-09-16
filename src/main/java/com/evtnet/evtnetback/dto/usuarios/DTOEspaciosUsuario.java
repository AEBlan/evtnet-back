// DTOEspaciosUsuario.java
package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DTOEspaciosUsuario {
    private List<Propietario> propietario;
    private List<Administrador> administrador;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Propietario {
        private Long id;
        private String nombre;
        private LocalDateTime fechaDesde; // = Espacio.fechaHoraAlta
        private LocalDateTime fechaHasta; // = Espacio.fechaHoraBaja
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Administrador {
        private Long id;
        private String nombre;
        private LocalDateTime fechaDesde; // del espacio
        private LocalDateTime fechaHasta; // del espacio
        private List<Periodo> periodos;   // alta/baja admin
    }

    @Data @AllArgsConstructor
    public static class Periodo {
        private LocalDateTime desde;
        private LocalDateTime hasta;
    }
}
