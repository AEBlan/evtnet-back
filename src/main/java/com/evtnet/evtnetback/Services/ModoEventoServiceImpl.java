package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ModoEvento;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.Repositories.ModoEventoRepository;

public class ModoEventoServiceImpl extends BaseServiceImpl <ModoEvento, Long> implements ModoEventoService {

    private  final ModoEventoRepository modoEventoRepository;
    
    public ModoEventoServiceImpl(BaseRepository<ModoEvento, Long> baseRepository) {
        super(baseRepository);
    }
    
}
