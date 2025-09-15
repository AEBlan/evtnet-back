package com.evtnet.evtnetback.Repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.evtnet.evtnetback.Entities.Espacio;

import com.evtnet.evtnetback.Repositories.BaseRepository;

@Repository
public interface EspacioRepository extends BaseRepository <Espacio, Long> {
    List<Espacio> findAllByPropietario_Username(String username);
}
