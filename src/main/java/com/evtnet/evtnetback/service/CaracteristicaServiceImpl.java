package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Caracteristica;
import com.evtnet.evtnetback.repository.BaseRepository;

public class CaracteristicaServiceImpl extends BaseServiceImpl <Caracteristica, Long> implements CaracteristicaService {

    public CaracteristicaServiceImpl(BaseRepository<Caracteristica, Long> baseRepository) {
        super(baseRepository);
      
    }
    
}
