package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.TipoUsuarioGrupo;
import com.evtnet.evtnetback.repository.BaseRepository;

public class TipoUsuarioGrupoServiceImpl extends BaseServiceImpl <TipoUsuarioGrupo, Long> implements TipoUsuarioGrupoService {

    public TipoUsuarioGrupoServiceImpl(BaseRepository<TipoUsuarioGrupo, Long> baseRepository) {
        super(baseRepository);
    }
    
}
