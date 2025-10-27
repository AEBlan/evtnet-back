package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.SEPEstado;
import com.evtnet.evtnetback.repository.BaseRepository;

public class SEPEstadoServiceImpl  extends BaseServiceImpl <SEPEstado, Long> implements SEPEstadoService  {

    public SEPEstadoServiceImpl(BaseRepository<SEPEstado, Long> baseRepository) {
        super(baseRepository);
    }
    
}
