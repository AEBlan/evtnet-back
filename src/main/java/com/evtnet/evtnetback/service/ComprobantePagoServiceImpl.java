package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.ComprobantePago;
import com.evtnet.evtnetback.repository.BaseRepository;

public class ComprobantePagoServiceImpl extends BaseServiceImpl <ComprobantePago, Long> implements ComprobantePagoService {

    public ComprobantePagoServiceImpl(BaseRepository<ComprobantePago, Long> baseRepository) {
        super(baseRepository);
    }
    
}
