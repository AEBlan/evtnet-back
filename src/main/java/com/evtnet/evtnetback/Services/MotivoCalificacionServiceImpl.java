package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.MotivoCalificacion;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class MotivoCalificacionServiceImpl extends BaseServiceImpl <MotivoCalificacion, Long> implements MotivoCalificacionService {

    public MotivoCalificacionServiceImpl(BaseRepository<MotivoCalificacion, Long> baseRepository) {
        super(baseRepository);
    }
    
}
