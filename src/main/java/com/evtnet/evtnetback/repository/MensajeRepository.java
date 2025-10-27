package com.evtnet.evtnetback.repository;

import java.util.List;

import com.evtnet.evtnetback.entity.Mensaje;

public interface MensajeRepository extends BaseRepository <Mensaje, Long> {
        List<Mensaje> findAllByChat_IdOrderByFechaHoraAsc(Long chatId);

}
