// eventos/DTOCrearEvento.java
package com.evtnet.evtnetback.dto.eventos;

import java.util.List;

public record DTOCrearEvento(
    String nombre,
    String descripcion,
    Long idEspacio,
    boolean usarCronograma,
    Long fechaDesde,
    Long fechaHasta,
    long horarioId,
    List<Long> disciplinas,
    String direccion,
    Ubicacion ubicacion,
    List<Long> modos,
    long tipoInscripcion,
    double precio,
    int maxParticipantes
) {
    public record Ubicacion(Double latitud, Double longitud) {}
}

