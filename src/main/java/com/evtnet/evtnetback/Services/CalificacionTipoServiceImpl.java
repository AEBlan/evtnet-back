package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.CalificacionTipo;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class CalificacionTipoServiceImpl extends BaseServiceImpl <CalificacionTipo, Long> implements CalificacionTipoService {

    public CalificacionTipoServiceImpl(BaseRepository<CalificacionTipo, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
