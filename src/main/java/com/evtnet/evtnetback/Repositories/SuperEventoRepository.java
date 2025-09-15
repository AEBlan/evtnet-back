package com.evtnet.evtnetback.Repositories;

import java.util.List;

import com.evtnet.evtnetback.Entities.SuperEvento;

import com.evtnet.evtnetback.Repositories.BaseRepository;

public interface SuperEventoRepository extends BaseRepository <SuperEvento, Long> {
    List<SuperEvento> findAllByUsuario_Username(String username);
}
