package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.TipoEspacio;

import java.util.Optional;

public interface TipoEspacioRepository extends BaseRepository<TipoEspacio, Long> {
    Optional<TipoEspacio> findByNombreIgnoreCase(String nombre);
}
