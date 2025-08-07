package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.HorarioEspacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class HorarioEspacioServiceImpl extends BaseServiceImpl <HorarioEspacio, Long> implements HorarioEspacioService {

    public HorarioEspacioServiceImpl(BaseRepository<HorarioEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
