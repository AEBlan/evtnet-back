package com.evtnet.evtnetback.dto.eventos;

import java.util.List;

public record DTODatosCreacionEvento(

    String nombreEspacio,
    List<SubEspacio> subespacios,

    Boolean requiereAprobarEventos,

    double comisionInscripcion,


    Boolean espacioPublico,
    Boolean administrador,

    Boolean tieneBasesYCondiciones
    
) {
    public record SubEspacio(
        Long id,
        String nombre,
        Long diasHaciaAdelante,
        int capacidadMaxima
    ) {}
}



