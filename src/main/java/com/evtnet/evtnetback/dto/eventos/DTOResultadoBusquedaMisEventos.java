package com.evtnet.evtnetback.dto.eventos;

public record DTOResultadoBusquedaMisEventos(
    long id,
    String nombre,
    long fechaDesde,
    long fechaHasta,
    String espacioNombre,
    String rol,
    Integer participantes
) {}

