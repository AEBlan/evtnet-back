package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.DenunciaEventoEstado;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class DenunciaEventoEstadoServiceImpl extends BaseServiceImpl <DenunciaEventoEstado, Long> implements DenunciaEventoEstadoService {

    public DenunciaEventoEstadoServiceImpl(BaseRepository<DenunciaEventoEstado, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
