package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Rol;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class RolServiceImpl extends BaseServiceImpl <Rol, Long> implements RolService  {

    public RolServiceImpl(BaseRepository<Rol, Long> baseRepository) {
        super(baseRepository);
    }
    
}
