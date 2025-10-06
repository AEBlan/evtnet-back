package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.CalificacionTipo;

import java.util.List;

public interface CalificacionTipoRepository extends BaseRepository<CalificacionTipo, Long> {
    // Tipos activos ordenados por nombre
    List<CalificacionTipo> findByFechaHoraBajaIsNullOrderByNombreAsc();
}