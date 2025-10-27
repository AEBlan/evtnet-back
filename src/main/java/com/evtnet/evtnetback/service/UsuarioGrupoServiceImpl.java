package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.UsuarioGrupo;
import com.evtnet.evtnetback.repository.BaseRepository;

public class UsuarioGrupoServiceImpl extends BaseServiceImpl <UsuarioGrupo, Long> implements UsuarioGrupoService {

    public UsuarioGrupoServiceImpl(BaseRepository<UsuarioGrupo, Long> baseRepository) {
        super(baseRepository);
    }
    
}
