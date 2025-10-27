package com.evtnet.evtnetback.repository;

import org.springframework.stereotype.Repository;
import com.evtnet.evtnetback.entity.RolUsuario;
import com.evtnet.evtnetback.entity.Rol;
import com.evtnet.evtnetback.entity.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;


@Repository
public interface RolUsuarioRepository extends BaseRepository<RolUsuario, Long> {
    @Query("""
           select distinct p.nombre
           from RolUsuario ru
           join ru.usuario u
           join ru.rol r
           join r.rolPermisos rp
           join rp.permiso p
           where u.username = :username
           """)
    List<String> findPermisosByUsername(@Param("username") String username);
    List<RolUsuario> findByUsuario(Usuario usuario);
    Optional<RolUsuario> findByUsuarioAndRol(Usuario usuario, Rol rol);
    void deleteByUsuarioAndRol(Usuario usuario, Rol rol);
    boolean existsByUsuarioAndRol(Usuario usuario, Rol rol);
    List<RolUsuario> findByUsuarioAndFechaHoraBajaIsNull(Usuario usuario);
    Optional<RolUsuario> findByUsuarioAndRolAndFechaHoraBajaIsNull(Usuario usuario, Rol rol);
}
