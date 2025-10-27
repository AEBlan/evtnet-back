package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.InstanciaMascota;
import com.evtnet.evtnetback.repository.BaseRepository;

public class InstanciaMascotaServiceImpl extends BaseServiceImpl <InstanciaMascota, Long> implements InstanciaMascotaService {

    public InstanciaMascotaServiceImpl(BaseRepository<InstanciaMascota, Long> baseRepository) {
        super(baseRepository);
    }
    
}
