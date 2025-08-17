package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.ModoEvento;
import com.evtnet.evtnetback.Repositories.ModoEventoRepository;
import org.springframework.stereotype.Service;

@Service
public class ModoEventoServiceImpl
        extends BaseServiceImpl<ModoEvento, Long>
        implements ModoEventoService {

    private final ModoEventoRepository modoEventoRepository;

    public ModoEventoServiceImpl(ModoEventoRepository modoEventoRepository) {
        super(modoEventoRepository);               
        this.modoEventoRepository = modoEventoRepository;
    }
}
