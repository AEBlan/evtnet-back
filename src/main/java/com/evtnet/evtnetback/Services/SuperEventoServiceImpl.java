package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.SuperEvento;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class SuperEventoServiceImpl extends BaseServiceImpl <SuperEvento,Long> implements SuperEventoService  {

    public SuperEventoServiceImpl(BaseRepository<SuperEvento, Long> baseRepository) {
        super(baseRepository);
    }
    
}
