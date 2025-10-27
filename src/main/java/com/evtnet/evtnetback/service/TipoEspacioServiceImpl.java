package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.TipoEspacio;
import com.evtnet.evtnetback.repository.BaseRepository;

public class TipoEspacioServiceImpl extends BaseServiceImpl <TipoEspacio, Long> implements TipoEspacioService {

    public TipoEspacioServiceImpl(BaseRepository<TipoEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
