package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.TipoAdministradorEvento;

import java.util.Optional;

public interface TipoAdministradorEventoRepository extends BaseRepository<TipoAdministradorEvento, Long> {
    Optional<TipoAdministradorEvento> findByNombreIgnoreCase(String nombre);
}
