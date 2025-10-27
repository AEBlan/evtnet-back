package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ResenaEspacio;
import com.evtnet.evtnetback.repository.BaseRepository;

public class ResenaEspacioServiceImpl extends BaseServiceImpl <ResenaEspacio, Long> implements ResenaEspacioService {

    public ResenaEspacioServiceImpl(BaseRepository<ResenaEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
