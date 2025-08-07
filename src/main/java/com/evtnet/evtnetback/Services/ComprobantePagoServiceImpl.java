package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ComprobantePago;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class ComprobantePagoServiceImpl extends BaseServiceImpl <ComprobantePago, Long> implements ComprobantePagoService {

    public ComprobantePagoServiceImpl(BaseRepository<ComprobantePago, Long> baseRepository) {
        super(baseRepository);
    }
    
}
