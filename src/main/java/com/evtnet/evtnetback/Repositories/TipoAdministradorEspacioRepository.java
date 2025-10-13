package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.TipoAdministradorEspacio;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoAdministradorEspacioRepository extends BaseRepository<TipoAdministradorEspacio, Long> {
    Optional<TipoAdministradorEspacio> findByNombre(String nombre);
}
