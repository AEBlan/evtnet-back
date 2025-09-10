package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Invitado;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class InvitadoServiceImpl extends BaseServiceImpl <Invitado, Long> implements InvitadoService {

    public InvitadoServiceImpl(BaseRepository<Invitado, Long> baseRepository) {
        super(baseRepository);
    }
    
}
