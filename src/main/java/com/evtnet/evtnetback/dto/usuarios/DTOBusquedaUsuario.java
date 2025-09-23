package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

import java.time.LocalDate;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class DTOBusquedaUsuario {
    private String username;
    private String nombre;
    private String apellido;
    private String mail;
    private String dni;
    private LocalDate fechaNacimiento;
}
