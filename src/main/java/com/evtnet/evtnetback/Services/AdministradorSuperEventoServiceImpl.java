package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.AdministradorSuperEvento;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class AdministradorSuperEventoServiceImpl extends BaseServiceImpl <AdministradorSuperEvento, Long> implements AdministradorSuperEventoService {

    public AdministradorSuperEventoServiceImpl(BaseRepository<AdministradorSuperEvento, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
