package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.AdministradorEspacio;
import com.evtnet.evtnetback.repository.BaseRepository;

public class AdministradorEspacioServiceImpl extends BaseServiceImpl <AdministradorEspacio,Long> implements AdministradorEspacioService {

    public AdministradorEspacioServiceImpl(BaseRepository<AdministradorEspacio, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
