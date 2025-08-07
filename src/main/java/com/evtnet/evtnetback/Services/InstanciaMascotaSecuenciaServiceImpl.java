package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.InstanciaMascotaSecuencia;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class InstanciaMascotaSecuenciaServiceImpl extends BaseServiceImpl <InstanciaMascotaSecuencia, Long> implements InstanciaMascotaSecuenciaService  {

    public InstanciaMascotaSecuenciaServiceImpl(BaseRepository<InstanciaMascotaSecuencia, Long> baseRepository) {
        super(baseRepository);
    }
    
}
