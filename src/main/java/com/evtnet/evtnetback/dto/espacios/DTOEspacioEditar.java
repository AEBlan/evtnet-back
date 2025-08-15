package com.evtnet.evtnetback.dto.espacios;

import java.util.List;

public record DTOEspacioEditar(
    long id,
    String nombre,
    String descripcion,
    String direccion,
    double latitud,
    double longitud,
    List<Disciplina> disciplinas,
    boolean esAdmin,
    boolean esPropietario,
    boolean esPublico
) {
    public record Disciplina(long id, String nombre) {}
}

