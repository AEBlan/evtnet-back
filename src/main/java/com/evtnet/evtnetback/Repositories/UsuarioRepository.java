package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Usuario;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, Long> {
    Optional<Usuario> findByMail(String mail);
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByMail(String mail);
}
