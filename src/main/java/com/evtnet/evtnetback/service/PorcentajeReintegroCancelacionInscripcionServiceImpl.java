package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.PorcentajeReintegroCancelacionInscripcion;
import com.evtnet.evtnetback.repository.BaseRepository;

public class PorcentajeReintegroCancelacionInscripcionServiceImpl extends BaseServiceImpl <PorcentajeReintegroCancelacionInscripcion, Long> implements PorcentajeReintegroCancelacionInscripcionService {

    public PorcentajeReintegroCancelacionInscripcionServiceImpl(
            BaseRepository<PorcentajeReintegroCancelacionInscripcion, Long> baseRepository) {
        super(baseRepository);
    }
    
}
