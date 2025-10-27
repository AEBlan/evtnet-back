package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.TipoAdministradorEvento;

import java.util.Optional;

public interface TipoAdministradorEventoRepository extends BaseRepository<TipoAdministradorEvento, Long> {
    Optional<TipoAdministradorEvento> findByNombreIgnoreCase(String nombre);
}
