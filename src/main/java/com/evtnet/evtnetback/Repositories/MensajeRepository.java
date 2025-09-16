package com.evtnet.evtnetback.Repositories;

import java.util.List;

import com.evtnet.evtnetback.Entities.Mensaje;

import com.evtnet.evtnetback.Repositories.BaseRepository;

public interface MensajeRepository extends BaseRepository <Mensaje, Long> {
        List<Mensaje> findAllByChat_IdOrderByFechaHoraAsc(Long chatId);

}
