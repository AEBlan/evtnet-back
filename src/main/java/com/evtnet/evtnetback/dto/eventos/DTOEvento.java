package com.evtnet.evtnetback.dto.eventos;

import java.util.List;

public record DTOEvento(
    String nombre,
    String descripcion,
    long fechaDesde,
    long fechaHasta,
    double precio,
    List<String> modos,
    List<String> disciplinas,
    Espacio espacio,
    String direccion,
    Ubicacion ubicacion,
    Superevento superevento,
    boolean inscripto,
    List<Inscripto> inscriptos,
    boolean administrador,
    Long idChat
) {
    public record Espacio(long id, String nombre) {}
    public record Ubicacion(Double latitud, Double longitud) {}
    public record Superevento(long id, String nombre) {}
    public record Inscripto(String username, String nombre, String apellido) {}
}

