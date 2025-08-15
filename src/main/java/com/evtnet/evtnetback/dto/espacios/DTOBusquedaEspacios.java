package com.evtnet.evtnetback.dto.espacios;

import java.util.List;

public record DTOBusquedaEspacios(
    String texto,
    Ubicacion ubicacion,
    List<Long> tipos,
    List<Long> disciplinas
) {
    public record Ubicacion(Double latitud, Double longitud, Double rango) {}
}

