package com.evtnet.evtnetback.dto.usuarios;

public record DTORegistrarse(
    String nombre,
    String apellido,
    String username,
    String dni,
    Long fechaNacimiento,
    String mail,
    String password
) {}

