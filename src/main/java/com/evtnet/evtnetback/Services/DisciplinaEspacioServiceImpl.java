package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.DisciplinaEspacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import org.springframework.stereotype.Service;

@Service
public class DisciplinaEspacioServiceImpl extends BaseServiceImpl <DisciplinaEspacio, Long> implements DisciplinaEspacioService {

    public DisciplinaEspacioServiceImpl(BaseRepository<DisciplinaEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
