package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.DisciplinaEvento;
import com.evtnet.evtnetback.repository.BaseRepository;

public class DisciplinaEventoServiceImpl extends BaseServiceImpl <DisciplinaEvento, Long> implements DisciplinaEventoService {

    public DisciplinaEventoServiceImpl(BaseRepository<DisciplinaEvento, Long> baseRepository) {
        super(baseRepository);
    }
    
}
