package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ResenaEspacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class ResenaEspacioServiceImpl extends BaseServiceImpl <ResenaEspacio, Long> implements ResenaEspacioService {

    public ResenaEspacioServiceImpl(BaseRepository<ResenaEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
