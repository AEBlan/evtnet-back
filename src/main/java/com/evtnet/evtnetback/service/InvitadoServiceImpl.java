package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Invitado;
import com.evtnet.evtnetback.repository.BaseRepository;

public class InvitadoServiceImpl extends BaseServiceImpl <Invitado, Long> implements InvitadoService {

    public InvitadoServiceImpl(BaseRepository<Invitado, Long> baseRepository) {
        super(baseRepository);
    }
    
}
