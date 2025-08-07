package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.EventoModoEvento;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class EventoModoEventoServiceImpl extends BaseServiceImpl <EventoModoEvento, Long> implements EventoModoEventoService {

    public EventoModoEventoServiceImpl(BaseRepository<EventoModoEvento, Long> baseRepository) {
        super(baseRepository);
    }
    
}
