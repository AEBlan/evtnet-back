package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.DisciplinaEvento;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class DisciplinaEventoServiceImpl extends BaseServiceImpl <DisciplinaEvento, Long> implements DisciplinaEventoService {

    public DisciplinaEventoServiceImpl(BaseRepository<DisciplinaEvento, Long> baseRepository) {
        super(baseRepository);
    }
    
}
