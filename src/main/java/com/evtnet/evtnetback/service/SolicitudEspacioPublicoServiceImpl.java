package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.SolicitudEspacioPublico;
import com.evtnet.evtnetback.repository.BaseRepository;

public class SolicitudEspacioPublicoServiceImpl extends BaseServiceImpl <SolicitudEspacioPublico, Long> implements SolicitudEspacioPublicoService {

    public SolicitudEspacioPublicoServiceImpl(BaseRepository<SolicitudEspacioPublico, Long> baseRepository) {
        super(baseRepository);
    }
    
}
