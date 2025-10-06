package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class DTOGruposUsuario {
    private List<GrupoDTO> grupos;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class GrupoDTO {
        private Long id;
        private String nombre;
        private List<RolDTO> roles;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class RolDTO {
        private String nombre;
        private LocalDateTime fechaDesde;
        private LocalDateTime fechaHasta; // null si no hay baja
    }
}
