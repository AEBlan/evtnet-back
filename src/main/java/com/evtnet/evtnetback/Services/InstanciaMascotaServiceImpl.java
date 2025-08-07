package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.InstanciaMascota;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class InstanciaMascotaServiceImpl extends BaseServiceImpl <InstanciaMascota, Long> implements InstanciaMascotaService {

    public InstanciaMascotaServiceImpl(BaseRepository<InstanciaMascota, Long> baseRepository) {
        super(baseRepository);
    }
    
}
