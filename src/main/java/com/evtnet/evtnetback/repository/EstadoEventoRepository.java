package com.evtnet.evtnetback.repository;

import org.springframework.stereotype.Repository;

import com.evtnet.evtnetback.entity.EstadoEvento;
import java.util.Optional;

@Repository
public interface EstadoEventoRepository extends BaseRepository <EstadoEvento, Long>{
    Optional<EstadoEvento> findByNombreIgnoreCase(String nombre);
}
