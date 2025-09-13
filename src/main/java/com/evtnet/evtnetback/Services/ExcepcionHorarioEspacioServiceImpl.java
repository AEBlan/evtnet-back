package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ExcepcionHorarioEspacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class ExcepcionHorarioEspacioServiceImpl extends BaseServiceImpl <ExcepcionHorarioEspacio, Long> implements ExcepcionHorarioEspacioService {

    public ExcepcionHorarioEspacioServiceImpl(BaseRepository<ExcepcionHorarioEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
