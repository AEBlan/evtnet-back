package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.DenunciaEventoEstado;
import com.evtnet.evtnetback.repository.BaseRepository;

public class DenunciaEventoEstadoServiceImpl extends BaseServiceImpl <DenunciaEventoEstado, Long> implements DenunciaEventoEstadoService {

    public DenunciaEventoEstadoServiceImpl(BaseRepository<DenunciaEventoEstado, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
