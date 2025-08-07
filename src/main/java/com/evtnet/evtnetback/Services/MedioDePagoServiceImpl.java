package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.MedioDePago;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class MedioDePagoServiceImpl extends BaseServiceImpl <MedioDePago, Long> implements MedioDePagoService {

    public MedioDePagoServiceImpl(BaseRepository<MedioDePago, Long> baseRepository) {
        super(baseRepository);
    }
    
}
