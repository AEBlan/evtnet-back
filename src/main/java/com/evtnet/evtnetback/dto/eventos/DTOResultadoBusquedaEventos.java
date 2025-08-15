package com.evtnet.evtnetback.dto.eventos;

import java.util.List;

public record DTOResultadoBusquedaEventos(
    boolean esSuperevento,
    long id,
    String nombre,
    Long fechaHoraInicio,
    Double precio,
    String nombreEspacio,
    List<String> disciplinas,
    Long fechaHoraProximoEvento
) {}

