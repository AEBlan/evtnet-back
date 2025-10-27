package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ExcepcionHorarioEspacio;
import com.evtnet.evtnetback.repository.BaseRepository;

public class ExcepcionHorarioEspacioServiceImpl extends BaseServiceImpl <ExcepcionHorarioEspacio, Long> implements ExcepcionHorarioEspacioService {

    public ExcepcionHorarioEspacioServiceImpl(BaseRepository<ExcepcionHorarioEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
