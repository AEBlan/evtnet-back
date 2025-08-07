package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.TipoUsuarioGrupo;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class TipoUsuarioGrupoServiceImpl extends BaseServiceImpl <TipoUsuarioGrupo, Long> implements TipoUsuarioGrupoService {

    public TipoUsuarioGrupoServiceImpl(BaseRepository<TipoUsuarioGrupo, Long> baseRepository) {
        super(baseRepository);
    }
    
}
