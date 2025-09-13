package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ParametroSistema;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class ParametroSistemaServiceImpl extends BaseServiceImpl <ParametroSistema, Long> implements ParametroSistemaService {

    public ParametroSistemaServiceImpl(BaseRepository<ParametroSistema, Long> baseRepository) {
        super(baseRepository);
    }
    
}
