package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Calificacion;
import com.evtnet.evtnetback.repository.BaseRepository;

public class CalificacionServiceImpl extends BaseServiceImpl <Calificacion, Long> implements CalificacionService {

    public CalificacionServiceImpl(BaseRepository<Calificacion, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
