package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOPerfil {
    private String username;
    private String nombre;
    private String apellido;
    private String mail;                    // puede ser null
    private String dni;                     // puede ser null
    private Long fechaNacimiento;         // epoch millis o null (front lo trata como Date)
    private List<ItemCalificacion> calificaciones; // null o lista

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ItemCalificacion {
        private String nombre;
        private Integer porcentaje;
    }
}
