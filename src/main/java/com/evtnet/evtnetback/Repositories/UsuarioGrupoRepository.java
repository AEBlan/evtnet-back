package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.UsuarioGrupo;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
}
