package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.AdministradorEspacio;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class AdministradorEspacioServiceImpl extends BaseServiceImpl <AdministradorEspacio,Long> implements AdministradorEspacioService {

    public AdministradorEspacioServiceImpl(BaseRepository<AdministradorEspacio, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
