package com.evtnet.evtnetback.dto.espacios;

import java.util.List;

public record DTOResultadoBusquedaMisEspacios(
    long id,
    String nombre,
    String rol,
    List<String> disciplinas
) {}
