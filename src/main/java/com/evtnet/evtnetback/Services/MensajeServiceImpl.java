package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Mensaje;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class MensajeServiceImpl extends BaseServiceImpl <Mensaje, Long> implements MensajeService {

    public MensajeServiceImpl(BaseRepository<Mensaje, Long> baseRepository) {
        super(baseRepository);
    }
    
}
