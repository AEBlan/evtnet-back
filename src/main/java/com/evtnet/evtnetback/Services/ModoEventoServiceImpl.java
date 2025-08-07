package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ModoEvento;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class ModoEventoServiceImpl extends BaseServiceImpl <ModoEvento, Long> implements ModoEventoService {

    public ModoEventoServiceImpl(BaseRepository<ModoEvento, Long> baseRepository) {
        super(baseRepository);
    }
    
}
