package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.HorarioEspacio;
import com.evtnet.evtnetback.repository.BaseRepository;

public class HorarioEspacioServiceImpl extends BaseServiceImpl <HorarioEspacio, Long> implements HorarioEspacioService {

    public HorarioEspacioServiceImpl(BaseRepository<HorarioEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
