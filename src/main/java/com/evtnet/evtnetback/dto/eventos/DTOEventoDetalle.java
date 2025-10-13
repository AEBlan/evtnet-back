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
    boolean cancelado,
    String motivoCancelacion,
    boolean cupoLleno,
    String rolUsuario,
    boolean puedeDenunciar,
    boolean puedeCancelarInscripcion,
    boolean puedeAdministrar,
    boolean puedeChatear,
    boolean puedeCompartir,
    List<Inscripto> inscriptos
) {
    public record Espacio(long id, String nombre, String direccion, Double latitud, Double longitud) {}
    public record Subespacio(long id, String nombre, String descripcion) {}
    public record Inscripto(String username, String nombre, String apellido, String fotoPerfil) {}
}
