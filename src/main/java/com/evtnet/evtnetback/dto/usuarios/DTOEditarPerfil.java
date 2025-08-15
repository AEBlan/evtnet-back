package com.evtnet.evtnetback.dto.usuarios;

public record DTOEditarPerfil(
    String nombre,
    String apellido,
    String dni,
    Long fechaNacimiento,
    String cbu
) {}
