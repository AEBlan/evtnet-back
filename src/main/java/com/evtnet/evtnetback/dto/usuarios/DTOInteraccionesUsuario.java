// src/main/java/com/evtnet/evtnetback/DTOs/DTOInteraccionesUsuario.java
package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class DTOInteraccionesUsuario {
    private List<InteraccionDTO> interacciones;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class InteraccionDTO {
        private Long id;
        private String nombre;
        private String tipo;           // "Evento" | "Espacio" | "SuperEvento" | "Grupo" | "Directo"
        private LocalDateTime fechaDesde;
        private LocalDateTime fechaHasta;
        private String username;       // para "Directo" → el otro participante (puede ser null en otros tipos)
    }
}
