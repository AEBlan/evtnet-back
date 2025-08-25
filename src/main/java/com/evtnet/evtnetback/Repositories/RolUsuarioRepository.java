package com.evtnet.evtnetback.Repositories;

import org.springframework.stereotype.Repository;
import com.evtnet.evtnetback.Entities.RolUsuario;
import com.evtnet.evtnetback.Entities.Rol;
import com.evtnet.evtnetback.Entities.Usuario;
import java.util.Optional;
import java.util.List;

@Repository
public interface RolUsuarioRepository extends BaseRepository<RolUsuario, Long> {
    List<RolUsuario> findByUsuario(Usuario usuario);
    Optional<RolUsuario> findByUsuarioAndRol(Usuario usuario, Rol rol);
    void deleteByUsuarioAndRol(Usuario usuario, Rol rol);
    boolean existsByUsuarioAndRol(Usuario usuario, Rol rol);
}
