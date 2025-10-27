package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.RolPermiso;
import com.evtnet.evtnetback.repository.BaseRepository;

public class RolPermisoServiceImpl extends BaseServiceImpl <RolPermiso, Long> implements RolPermisoService {

    public RolPermisoServiceImpl(BaseRepository<RolPermiso, Long> baseRepository) {
        super(baseRepository);
    }
    
}
