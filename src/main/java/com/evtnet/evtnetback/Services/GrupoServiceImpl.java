package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Grupo;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class GrupoServiceImpl extends BaseServiceImpl <Grupo, Long> implements GrupoService {

    public GrupoServiceImpl(BaseRepository<Grupo, Long> baseRepository) {
        super(baseRepository);
    }
    
}
