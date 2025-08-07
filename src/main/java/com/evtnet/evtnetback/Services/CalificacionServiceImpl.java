package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Calificacion;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class CalificacionServiceImpl extends BaseServiceImpl <Calificacion, Long> implements CalificacionService {

    public CalificacionServiceImpl(BaseRepository<Calificacion, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
