package com.evtnet.evtnetback.dto.usuarios;

import java.util.List;

public record DTOPerfil(
    String username,
    String nombre,
    String apellido,
    String mail,
    String dni,
    Long fechaNacimiento,
    List<Calificacion> calificaciones
) {
    public record Calificacion(String nombre, double porcentaje) {}
}

