package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Inscripcion;
import com.evtnet.evtnetback.repository.BaseRepository;

public class InscripcionServiceImpl extends BaseServiceImpl <Inscripcion, Long> implements InscripcionService {

    public InscripcionServiceImpl(BaseRepository<Inscripcion, Long> baseRepository) {
        super(baseRepository);
    }
    
}
