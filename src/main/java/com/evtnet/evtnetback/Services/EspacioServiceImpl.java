package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Espacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class EspacioServiceImpl extends BaseServiceImpl <Espacio, Long> implements EspacioService {

    public EspacioServiceImpl(BaseRepository<Espacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
