package com.evtnet.evtnetback.dto.espacios;

public record DTOVerificacionVigencia(
    boolean cronogramasSuperpuestos,
    boolean eventosProgramados
) {}

