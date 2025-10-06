package com.evtnet.evtnetback.dto.eventos;

public record DTOBusquedaMisEventos(
    String texto,
    Long fechaDesde,
    Long fechaHasta,
    boolean organizador,
    boolean administrador,
    boolean participante
) {}

