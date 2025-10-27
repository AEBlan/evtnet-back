package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.DenunciaEvento;
import com.evtnet.evtnetback.repository.BaseRepository;

public class DenunciaEventoServiceImpl extends BaseServiceImpl <DenunciaEvento, Long> implements DenunciaEventoService {

    public DenunciaEventoServiceImpl(BaseRepository<DenunciaEvento, Long> baseRepository) {
        super(baseRepository);
    }
    
}
