package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Evento;
import com.evtnet.evtnetback.Repositories.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventoServiceImpl extends BaseServiceImpl <Evento, Long> implements EventoService {

    @Autowired
    public EventoServiceImpl(EventoRepository eventoRepository) {
        super(eventoRepository);
    }
    
}
