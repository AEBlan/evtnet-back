package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Permiso;
import com.evtnet.evtnetback.repository.BaseRepository;

public class PermisoServiceImpl extends BaseServiceImpl <Permiso, Long> implements PermisoService {

    public PermisoServiceImpl(BaseRepository<Permiso, Long> baseRepository) {
        super(baseRepository);
    }
    
}
