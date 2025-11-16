package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.InstanciaMascota;

import java.util.Optional;

public interface InstanciaMascotaRepository extends BaseRepository <InstanciaMascota, Long> {
    Optional<InstanciaMascota> findByNombre(String nombre);
}
