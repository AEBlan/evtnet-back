package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.PorcentajeReintegroCancelacionInscripcion;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class PorcentajeReintegroCancelacionInscripcionServiceImpl extends BaseServiceImpl <PorcentajeReintegroCancelacionInscripcion, Long> implements PorcentajeReintegroCancelacionInscripcionService {

    public PorcentajeReintegroCancelacionInscripcionServiceImpl(
            BaseRepository<PorcentajeReintegroCancelacionInscripcion, Long> baseRepository) {
        super(baseRepository);
    }
    
}
