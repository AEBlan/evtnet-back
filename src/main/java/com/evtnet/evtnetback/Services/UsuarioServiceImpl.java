package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Repositories.BaseRepository;

public class UsuarioServiceImpl extends BaseServiceImpl <Usuario, Long> implements UsuarioService {

    public UsuarioServiceImpl(BaseRepository<Usuario, Long> baseRepository) {
        super(baseRepository);
    }
    
}
