package com.evtnet.evtnetback.dto.eventos;

import java.util.List;

import lombok.Builder;

@Builder
public record DTOResultadoBusquedaEventos(
    boolean esSuperevento,
    long id,
    String nombre,
    Long fechaHoraInicio,
    Double precio,
    String nombreEspacio,
    List<String> disciplinas,
    Long fechaHoraProximoEvento,
    Double puntuacion //Solo usado por back-end
) {}

