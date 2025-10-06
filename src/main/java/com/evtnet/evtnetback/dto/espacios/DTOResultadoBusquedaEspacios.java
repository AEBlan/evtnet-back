package com.evtnet.evtnetback.dto.espacios;

import java.util.List;

public record DTOResultadoBusquedaEspacios(
    long id,
    String nombre,
    String tipo,
    List<String> disciplinas
) {}

