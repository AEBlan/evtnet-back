package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.AdministradorSuperEvento;
import com.evtnet.evtnetback.repository.BaseRepository;

public class AdministradorSuperEventoServiceImpl extends BaseServiceImpl <AdministradorSuperEvento, Long> implements AdministradorSuperEventoService {

    public AdministradorSuperEventoServiceImpl(BaseRepository<AdministradorSuperEvento, Long> baseRepository) {
        super(baseRepository);
        
    }
    
}
