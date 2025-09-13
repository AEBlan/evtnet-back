package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOModificarUsuario {
    private String username;       // username actual
    private String usernameNuevo;  // si se quiere cambiar
    private String nombre;
    private String apellido;
    private String mail;
    private String dni;
    private LocalDate fechaNacimiento;
    private List<Long> roles; // IDs de roles
}
