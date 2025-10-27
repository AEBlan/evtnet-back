package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Rol;
import com.evtnet.evtnetback.repository.BaseRepository;

public class RolServiceImpl extends BaseServiceImpl <Rol, Long> implements RolService  {

    public RolServiceImpl(BaseRepository<Rol, Long> baseRepository) {
        super(baseRepository);
    }
    
}
