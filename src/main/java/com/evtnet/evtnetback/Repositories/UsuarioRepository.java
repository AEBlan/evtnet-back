package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Usuario;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {
}
