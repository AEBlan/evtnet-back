package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.DenunciaEvento;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class DenunciaEventoServiceImpl extends BaseServiceImpl <DenunciaEvento, Long> implements DenunciaEventoService {

    public DenunciaEventoServiceImpl(BaseRepository<DenunciaEvento, Long> baseRepository) {
        super(baseRepository);
    }
    
}
