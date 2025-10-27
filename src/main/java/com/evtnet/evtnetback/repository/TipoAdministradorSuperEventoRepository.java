package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.TipoAdministradorSuperEvento;

import java.util.Optional;

public interface TipoAdministradorSuperEventoRepository extends BaseRepository<TipoAdministradorSuperEvento, Long> {
    Optional<TipoAdministradorSuperEvento> findByNombreIgnoreCase(String nombre);
}
