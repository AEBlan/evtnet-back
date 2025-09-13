package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.AdministradorEvento;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class AdministradorEventoServiceImpl extends BaseServiceImpl <AdministradorEvento, Long> implements AdministradorEventoService {

    public AdministradorEventoServiceImpl(BaseRepository<AdministradorEvento, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
