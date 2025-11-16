package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.EventoMascota;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventoMascotaRepository extends BaseRepository <EventoMascota, Long> {
    Optional<EventoMascota> findByNombre(String nombre);
}
