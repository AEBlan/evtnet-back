package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.UsuarioInstanciaMascota;
import com.evtnet.evtnetback.repository.BaseRepository;

public class UsuarioInstanciaMascotaServiceImpl extends BaseServiceImpl <UsuarioInstanciaMascota, Long> implements UsuarioInstanciaMascotaService  {

    public UsuarioInstanciaMascotaServiceImpl(BaseRepository<UsuarioInstanciaMascota, Long> baseRepository) {
        super(baseRepository);
    }
    
}
