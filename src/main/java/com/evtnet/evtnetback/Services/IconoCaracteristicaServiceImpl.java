package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.IconoCaracteristica;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class IconoCaracteristicaServiceImpl extends BaseServiceImpl <IconoCaracteristica, Long> implements IconoCaracteristicaService {

    public IconoCaracteristicaServiceImpl(BaseRepository<IconoCaracteristica, Long> baseRepository) {
        super(baseRepository);
    }
    
}
