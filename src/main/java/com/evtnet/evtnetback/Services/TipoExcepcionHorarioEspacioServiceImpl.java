package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.TipoExcepcionHorarioEspacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class TipoExcepcionHorarioEspacioServiceImpl extends BaseServiceImpl <TipoExcepcionHorarioEspacio, Long> implements TipoExcepcionHorarioEspacioService {

    public TipoExcepcionHorarioEspacioServiceImpl(BaseRepository<TipoExcepcionHorarioEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
