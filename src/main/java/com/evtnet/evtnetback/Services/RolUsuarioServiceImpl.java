package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.RolUsuario;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class RolUsuarioServiceImpl extends BaseServiceImpl <RolUsuario, Long> implements RolUsuarioService {

    public RolUsuarioServiceImpl(BaseRepository<RolUsuario, Long> baseRepository) {
        super(baseRepository);
    }
    
}
