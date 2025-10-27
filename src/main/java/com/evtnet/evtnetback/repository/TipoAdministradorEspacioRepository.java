package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.TipoAdministradorEspacio;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoAdministradorEspacioRepository extends BaseRepository<TipoAdministradorEspacio, Long> {
    Optional<TipoAdministradorEspacio> findByNombre(String nombre);
}
