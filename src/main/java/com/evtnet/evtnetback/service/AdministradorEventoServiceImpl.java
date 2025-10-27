package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.AdministradorEvento;
import com.evtnet.evtnetback.repository.BaseRepository;

public class AdministradorEventoServiceImpl extends BaseServiceImpl <AdministradorEvento, Long> implements AdministradorEventoService {

    public AdministradorEventoServiceImpl(BaseRepository<AdministradorEvento, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
