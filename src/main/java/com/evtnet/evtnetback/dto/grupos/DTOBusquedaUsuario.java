// src/main/java/com/evtnet/evtnetback/dto/usuarios/DTOBusquedaUsuario.java
package com.evtnet.evtnetback.dto.grupos;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOBusquedaUsuario {
    private String username;
    private String nombre;
    private String apellido;
    private String fotoPerfil; // puede venir null
}
