package com.evtnet.evtnetback.Repositories;


import java.util.Optional;

import com.evtnet.evtnetback.Entities.TipoAdministradorEspacio;

public interface TipoAdministradorEspacioRepository extends BaseRepository<TipoAdministradorEspacio, Long> {
    Optional<TipoAdministradorEspacio> findByNombreIgnoreCase(String nombre);
}
