package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOEditarPerfil {
    private String nombre;
    private String apellido;
    private String dni;
    private String cbu;
    private String telefono;
    private String bio;
    // si quiere cambiarla
    private String newPassword;
    private String currentPassword;
}