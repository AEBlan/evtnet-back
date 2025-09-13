package com.evtnet.evtnetback.dto.espacios;

import java.util.List;

public record DTOCrearEspacio(
    String nombre,
    String descripcion,
    String direccion,
    Double latitud,
    Double longitud,
    List<Long> disciplinas
) {}
