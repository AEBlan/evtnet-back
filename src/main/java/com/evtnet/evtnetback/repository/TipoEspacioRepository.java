package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.TipoEspacio;

import java.util.Optional;

public interface TipoEspacioRepository extends BaseRepository<TipoEspacio, Long> {
    Optional<TipoEspacio> findByNombre(String nombre);
}
