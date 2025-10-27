package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.TipoExcepcionHorarioEspacio;
import com.evtnet.evtnetback.repository.BaseRepository;

public class TipoExcepcionHorarioEspacioServiceImpl extends BaseServiceImpl <TipoExcepcionHorarioEspacio, Long> implements TipoExcepcionHorarioEspacioService {

    public TipoExcepcionHorarioEspacioServiceImpl(BaseRepository<TipoExcepcionHorarioEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
