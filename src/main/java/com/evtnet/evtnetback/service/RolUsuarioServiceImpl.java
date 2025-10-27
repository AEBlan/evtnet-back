package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.RolUsuario;
import com.evtnet.evtnetback.repository.BaseRepository;

public class RolUsuarioServiceImpl extends BaseServiceImpl <RolUsuario, Long> implements RolUsuarioService {

    public RolUsuarioServiceImpl(BaseRepository<RolUsuario, Long> baseRepository) {
        super(baseRepository);
    }
    
}
