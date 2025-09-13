package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Permiso;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class PermisoServiceImpl extends BaseServiceImpl <Permiso, Long> implements PermisoService {

    public PermisoServiceImpl(BaseRepository<Permiso, Long> baseRepository) {
        super(baseRepository);
    }
    
}
