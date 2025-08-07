package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.SolicitudEspacioPublico;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class SolicitudEspacioPublicoServiceImpl extends BaseServiceImpl <SolicitudEspacioPublico, Long> implements SolicitudEspacioPublicoService {

    public SolicitudEspacioPublicoServiceImpl(BaseRepository<SolicitudEspacioPublico, Long> baseRepository) {
        super(baseRepository);
    }
    
}
