package com.evtnet.evtnetback.dto.eventos;

import java.util.List;

public record DTODatosCreacionEvento(
    String nombreEspacio,
    //List<TipoInscripcion> tiposInscripcion,
    double comisionInscripcion,
    Boolean espacioPublico,
    Boolean administrador,
    int diasHaciaAdelante
) {
   // public record TipoInscripcion(long id, String nombre) {}
}

