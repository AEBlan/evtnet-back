package com.evtnet.evtnetback.dto.eventos;

import java.util.List;

public record DTOEventoDetalle(
    long id,
    String nombre,
    String descripcion,
    long fechaHoraInicio,
    long fechaHoraFin,
    double precioBase,
    double precioTotal,
    List<String> disciplinas,
    Espacio espacio,
    Subespacio subespacio,
    String estado,
    String motivoCancelacion,
    boolean cupoLleno,
    SuperEvento superevento,
    boolean inscripto,
    List<Inscripto> inscriptos,
    
    boolean administrador,
    boolean organizador,
    Long idChat
) {
    public record Espacio(long id, String nombre, String direccion, Double latitud, Double longitud) {}
    public record Subespacio(long id, String nombre, String descripcion) {}
    public record SuperEvento(long id, String nombre) {}
    public record Inscripto(String username, String nombre, String apellido) {}
}
