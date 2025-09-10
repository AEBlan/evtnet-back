package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.RolPermiso;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class RolPermisoServiceImpl extends BaseServiceImpl <RolPermiso, Long> implements RolPermisoService {

    public RolPermisoServiceImpl(BaseRepository<RolPermiso, Long> baseRepository) {
        super(baseRepository);
    }
    
}
