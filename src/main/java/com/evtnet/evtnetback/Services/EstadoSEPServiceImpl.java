package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.EstadoSEP;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class EstadoSEPServiceImpl extends BaseServiceImpl <EstadoSEP, Long> implements EstadoSEPService {

    public EstadoSEPServiceImpl(BaseRepository<EstadoSEP, Long> baseRepository) {
        super(baseRepository);
    }
    
}
