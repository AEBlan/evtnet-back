package com.evtnet.evtnetback.dto.grupos;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTOGrupoSimple {
    private Long id;
    private String nombre;
    private String descripcion;
    private CreadorDTO creador;
    private LocalDateTime fechaAlta;
    private LocalDateTime fechaBaja;

    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    public static class CreadorDTO {
        private String nombre;
        private String apellido;
    }
}
