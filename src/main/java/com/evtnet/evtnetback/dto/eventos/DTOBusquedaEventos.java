package com.evtnet.evtnetback.dto.eventos;

import java.util.List;

public record DTOBusquedaEventos(
    String texto,
    Ubicacion ubicacion,
    Long fechaDesde,
    Long fechaHasta,
    Long horaDesde,
    Long horaHasta,
    List<Long> tiposEspacio,
    boolean espaciosNoRegistrados,
    List<Long> disciplinas,
    List<Long> modos,
    Double precioLimite,
    boolean buscarEventos,
    boolean buscarSuperventos
) {
    public record Ubicacion(Double latitud, Double longitud, Double rango) {}
}
