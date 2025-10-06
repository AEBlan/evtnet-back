package com.evtnet.evtnetback.dto.grupos;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTOAdminGrupo {
    private Long id;
    private String nombre;
    private String descripcion;

    private CreadorDTO creador;
    private List<MiembroDTO> miembros;

    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    public static class CreadorDTO {
        private String nombre;
        private String apellido;
        private String username;
        private String mail;
    }

    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    public static class MiembroDTO {
        private String nombre;
        private String apellido;
        private String username;
        private String mail;
        private String tipo;
    }
}
