package com.evtnet.evtnetback.Repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.evtnet.evtnetback.Entities.Espacio;

@Repository
public interface EspacioRepository extends BaseRepository <Espacio, Long> {
    List<Espacio> findAllByPropietario_Username(String username);
    boolean existsByIdAndPropietario_Username(Long id, String username);
}
