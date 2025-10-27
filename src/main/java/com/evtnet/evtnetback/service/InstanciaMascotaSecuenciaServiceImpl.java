package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.InstanciaMascotaSecuencia;
import com.evtnet.evtnetback.repository.BaseRepository;

public class InstanciaMascotaSecuenciaServiceImpl extends BaseServiceImpl <InstanciaMascotaSecuencia, Long> implements InstanciaMascotaSecuenciaService  {

    public InstanciaMascotaSecuenciaServiceImpl(BaseRepository<InstanciaMascotaSecuencia, Long> baseRepository) {
        super(baseRepository);
    }
    
}
