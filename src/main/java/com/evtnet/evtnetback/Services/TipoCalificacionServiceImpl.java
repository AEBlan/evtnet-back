package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.TipoCalificacion;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class TipoCalificacionServiceImpl extends BaseServiceImpl <TipoCalificacion, Long> implements TipoCalificacionService {

    public TipoCalificacionServiceImpl(BaseRepository<TipoCalificacion, Long> baseRepository) {
        super(baseRepository);
    }
    
}
