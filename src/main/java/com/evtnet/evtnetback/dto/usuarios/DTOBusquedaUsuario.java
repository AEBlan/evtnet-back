// DTOResultadoBusquedaUsuario.java
package com.evtnet.evtnetback.dto.usuarios;

import java.time.LocalDate;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOBusquedaUsuario {
    private String username;
    private String nombre;
    private String apellido;
    private String mail;
    private String dni;
    private LocalDate fechaNacimiento;  
    //private Long fechaNacimiento;    // epoch ms (front lo mapea a Date)
}
