package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.EstadoDenunciaEvento;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class EstadoDenunciaEventoServiceImpl extends BaseServiceImpl <EstadoDenunciaEvento, Long> implements EstadoDenunciaEventoService {

    public EstadoDenunciaEventoServiceImpl(BaseRepository<EstadoDenunciaEvento, Long> baseRepository) {
        super(baseRepository);
    }
    
}
