package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.DisciplinaSubEspacio;
import com.evtnet.evtnetback.repository.BaseRepository;
import org.springframework.stereotype.Service;

@Service
public class DisciplinaSubEspacioServiceImpl extends BaseServiceImpl <DisciplinaSubEspacio, Long> implements DisciplinaSubEspacioService {

    public DisciplinaSubEspacioServiceImpl(BaseRepository<DisciplinaSubEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
