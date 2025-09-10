package com.evtnet.evtnetback.dto.espacios;

public record DTOBusquedaMisEspacios(
    String texto,
    boolean propietario,
    boolean administrador
) {}

