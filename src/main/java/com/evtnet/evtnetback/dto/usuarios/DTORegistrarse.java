package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTORegistrarse {
    private String nombre;
    private String apellido;
    private String username;
    private String dni;
    private String fechaNacimiento; // ISO-8601 o null (ej: "2001-03-05T00:00:00.000Z")
    private String mail;
    private String password;
}
