package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ReseñaEspacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class ReseñaEspacioServiceImpl extends BaseServiceImpl <ReseñaEspacio, Long> implements ReseñaEspacioService {

    public ReseñaEspacioServiceImpl(BaseRepository<ReseñaEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
