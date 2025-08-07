package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.SEPEstado;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class SEPEstadoServiceImpl  extends BaseServiceImpl <SEPEstado, Long> implements SEPEstadoService  {

    public SEPEstadoServiceImpl(BaseRepository<SEPEstado, Long> baseRepository) {
        super(baseRepository);
    }
    
}
