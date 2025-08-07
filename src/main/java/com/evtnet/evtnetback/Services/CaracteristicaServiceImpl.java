package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Caracteristica;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class CaracteristicaServiceImpl extends BaseServiceImpl <Caracteristica, Long> implements CaracteristicaService {

    public CaracteristicaServiceImpl(BaseRepository<Caracteristica, Long> baseRepository) {
        super(baseRepository);
      
    }
    
}
