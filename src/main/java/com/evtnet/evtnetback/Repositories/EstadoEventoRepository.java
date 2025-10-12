package com.evtnet.evtnetback.Repositories;

import org.springframework.stereotype.Repository;

import com.evtnet.evtnetback.Entities.EstadoEvento;
import java.util.Optional;

@Repository
public interface EstadoEventoRepository extends BaseRepository <EstadoEvento, Long>{
    Optional<EstadoEvento> findByNombreIgnoreCase(String nombre);
}
