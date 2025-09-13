package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.TipoEspacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class TipoEspacioServiceImpl extends BaseServiceImpl <TipoEspacio, Long> implements TipoEspacioService {

    public TipoEspacioServiceImpl(BaseRepository<TipoEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
