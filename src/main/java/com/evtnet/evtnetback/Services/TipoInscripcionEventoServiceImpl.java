package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.TipoInscripcionEvento;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class TipoInscripcionEventoServiceImpl extends BaseServiceImpl <TipoInscripcionEvento, Long> implements TipoInscripcionEventoService {

    public TipoInscripcionEventoServiceImpl(BaseRepository<TipoInscripcionEvento, Long> baseRepository) {
        super(baseRepository);
    }
    
}
