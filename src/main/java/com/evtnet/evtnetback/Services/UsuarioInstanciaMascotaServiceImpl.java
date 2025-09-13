package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.UsuarioInstanciaMascota;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class UsuarioInstanciaMascotaServiceImpl extends BaseServiceImpl <UsuarioInstanciaMascota, Long> implements UsuarioInstanciaMascotaService  {

    public UsuarioInstanciaMascotaServiceImpl(BaseRepository<UsuarioInstanciaMascota, Long> baseRepository) {
        super(baseRepository);
    }
    
}
