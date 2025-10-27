package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Mensaje;
import com.evtnet.evtnetback.repository.BaseRepository;

public class MensajeServiceImpl extends BaseServiceImpl <Mensaje, Long> implements MensajeService {

    public MensajeServiceImpl(BaseRepository<Mensaje, Long> baseRepository) {
        super(baseRepository);
    }
    
}
