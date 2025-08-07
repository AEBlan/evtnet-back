package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ComisionPorOrganizacion;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class ComisionPorOrganizacionServiceImpl extends BaseServiceImpl <ComisionPorOrganizacion, Long> implements ComisionPorOrganizacionService {

    public ComisionPorOrganizacionServiceImpl(BaseRepository<ComisionPorOrganizacion, Long> baseRepository) {
        super(baseRepository);
    }
    
}
