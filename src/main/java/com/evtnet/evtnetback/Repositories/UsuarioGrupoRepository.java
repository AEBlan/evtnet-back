package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.UsuarioGrupo;

import com.evtnet.evtnetback.Repositories.BaseRepository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioGrupoRepository extends BaseRepository <UsuarioGrupo, Long> {
     // Trae todo lo necesario para armar DTOGruposUsuario en un único query
    @Query("""
           select ug from UsuarioGrupo ug
           join fetch ug.usuario u
           join fetch ug.grupo g
           left join fetch ug.tipoUsuarioGrupo tug
           where u.username = :username
           """)
    List<UsuarioGrupo> findAllForUsername(@Param("username") String username);
    // Vínculo activo del usuario en el grupo
    @Query("""
           select ug
           from UsuarioGrupo ug
           where ug.grupo.id = :grupoId
             and ug.usuario.username = :username
             and ug.fechaHoraBaja is null
           """)
    Optional<UsuarioGrupo> findActivo(@Param("grupoId") Long grupoId,
                                      @Param("username") String username);

    // ¿Usuario (activo) es admin del grupo?
    @Query("""
           select (count(ug) > 0)
           from UsuarioGrupo ug
           where ug.grupo.id = :grupoId
             and ug.usuario.username = :username
             and ug.fechaHoraBaja is null
             and lower(ug.tipoUsuarioGrupo.nombre) = 'administrador'
           """)
    boolean esAdmin(@Param("grupoId") Long grupoId,
                    @Param("username") String username);

    // Miembros activos del grupo
    @Query("""
           select ug
           from UsuarioGrupo ug
           where ug.grupo.id = :grupoId
             and ug.fechaHoraBaja is null
           """)
    List<UsuarioGrupo> miembrosActivos(@Param("grupoId") Long grupoId);

    // Primera fecha de unión (primer alta) de ese usuario al grupo
    @Query("""
           select min(ug.fechaHoraAlta)
           from UsuarioGrupo ug
           where ug.grupo.id = :grupoId
             and ug.usuario.id = :usuarioId
           """)
    LocalDateTime primeraUnion(@Param("grupoId") Long grupoId,
                               @Param("usuarioId") Long usuarioId);
    List<UsuarioGrupo> findAllByGrupo_IdAndUsuario_IdOrderByFechaHoraAltaDesc(Long grupoId, Long usuarioId);
    Optional<UsuarioGrupo> findByGrupo_IdAndUsuario_IdAndFechaHoraBajaIsNull(Long grupoId, Long usuarioId);
    @Query("""
       select distinct ug
       from UsuarioGrupo ug
       join fetch ug.usuario u
       join fetch ug.grupo g
       join fetch ug.tipoUsuarioGrupo t
       where g.id = :grupoId
       and ug.fechaHoraBaja is null
       """)
    List<UsuarioGrupo> miembrosActivosConTodo(@Param("grupoId") Long grupoId);

}
