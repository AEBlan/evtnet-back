package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.DisciplinaSubEspacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import org.springframework.stereotype.Service;

@Service
public class DisciplinaSubEspacioServiceImpl extends BaseServiceImpl <DisciplinaSubEspacio, Long> implements DisciplinaSubEspacioService {

    public DisciplinaSubEspacioServiceImpl(BaseRepository<DisciplinaSubEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
