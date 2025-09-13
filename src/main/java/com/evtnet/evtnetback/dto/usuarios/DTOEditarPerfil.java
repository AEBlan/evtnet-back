package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOEditarPerfil {
    private String nombre;
    private String apellido;
    private String dni;
    private String cbu;
    private Long   fechaNacimiento; // epoch millis opcional

}
