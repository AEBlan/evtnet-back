package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Inscripcion;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class InscripcionServiceImpl extends BaseServiceImpl <Inscripcion, Long> implements InscripcionService {

    public InscripcionServiceImpl(BaseRepository<Inscripcion, Long> baseRepository) {
        super(baseRepository);
    }
    
}
