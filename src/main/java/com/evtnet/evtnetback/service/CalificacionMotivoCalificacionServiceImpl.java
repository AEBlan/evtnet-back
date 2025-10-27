package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.CalificacionMotivoCalificacion;
import com.evtnet.evtnetback.repository.BaseRepository;

public class CalificacionMotivoCalificacionServiceImpl extends BaseServiceImpl <CalificacionMotivoCalificacion, Long> implements CalificacionMotivoCalificacionService {

    public CalificacionMotivoCalificacionServiceImpl(
            BaseRepository<CalificacionMotivoCalificacion, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
