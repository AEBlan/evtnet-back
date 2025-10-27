package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.CalificacionTipo;

import java.util.List;

public interface CalificacionTipoRepository extends BaseRepository<CalificacionTipo, Long> {
    // Tipos activos ordenados por nombre
    List<CalificacionTipo> findByFechaHoraBajaIsNullOrderByNombreAsc();
}