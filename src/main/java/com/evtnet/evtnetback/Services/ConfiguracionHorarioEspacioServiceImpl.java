package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ConfiguracionHorarioEspacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class ConfiguracionHorarioEspacioServiceImpl extends BaseServiceImpl <ConfiguracionHorarioEspacio, Long> implements ConfiguracionHorarioEspacioService {

    public ConfiguracionHorarioEspacioServiceImpl(BaseRepository<ConfiguracionHorarioEspacio, Long> baseRepository) {
        super(baseRepository);
    }
    
}
