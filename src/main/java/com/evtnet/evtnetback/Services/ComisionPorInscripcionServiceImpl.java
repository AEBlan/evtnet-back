package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ComisionPorInscripcion;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class ComisionPorInscripcionServiceImpl extends BaseServiceImpl <ComisionPorInscripcion, Long> implements ComisionPorInscripcionService {

    public ComisionPorInscripcionServiceImpl(BaseRepository<ComisionPorInscripcion, Long> baseRepository) {
        super(baseRepository);
    }
    
}
