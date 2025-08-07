package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.CalificacionMotivoCalificacion;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class CalificacionMotivoCalificacionServiceImpl extends BaseServiceImpl <CalificacionMotivoCalificacion, Long> implements CalificacionMotivoCalificacionService {

    public CalificacionMotivoCalificacionServiceImpl(
            BaseRepository<CalificacionMotivoCalificacion, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
