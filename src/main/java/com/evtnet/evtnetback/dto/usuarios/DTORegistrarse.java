package com.evtnet.evtnetback.dto.usuarios;


import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTORegistrarse{
    private String nombre;
    private String apellido;
    private String username;
    private String dni;
    private Long fechaNacimiento;
    private String mail;
    private String password;
}

