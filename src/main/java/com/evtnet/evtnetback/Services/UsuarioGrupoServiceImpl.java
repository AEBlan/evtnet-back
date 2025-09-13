package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.UsuarioGrupo;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class UsuarioGrupoServiceImpl extends BaseServiceImpl <UsuarioGrupo, Long> implements UsuarioGrupoService {

    public UsuarioGrupoServiceImpl(BaseRepository<UsuarioGrupo, Long> baseRepository) {
        super(baseRepository);
    }
    
}
