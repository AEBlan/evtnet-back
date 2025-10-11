package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.TipoAdministradorSuperEvento;

import java.util.Optional;

public interface TipoAdministradorSuperEventoRepository extends BaseRepository<TipoAdministradorSuperEvento, Long> {
    Optional<TipoAdministradorSuperEvento> findByNombreIgnoreCase(String nombre);
}
