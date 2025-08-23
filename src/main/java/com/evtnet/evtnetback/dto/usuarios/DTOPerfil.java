package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOPerfil {
    private String username;
    private String nombre;
    private String apellido;
    private String mail;
    private String fotoPerfil; // nombre del archivo
    private String nombreCompleto;
    private String fotoUrl;
}
