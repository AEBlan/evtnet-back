package com.evtnet.evtnetback.dto.espacios;

import java.util.List;

public record DTOEspacioDetalle(
    String nombre,
    String tipoEspacio,
    String descripcion,
    String direccion,
    double latitud,
    double longitud,
    int cantidadImagenes,
    List<String> disciplinas,
    List<Caracteristica> caracteristicas,
    boolean esAdmin,
    Long idChat
) {
    public record Caracteristica(long imagenId, String nombre) {}
}
