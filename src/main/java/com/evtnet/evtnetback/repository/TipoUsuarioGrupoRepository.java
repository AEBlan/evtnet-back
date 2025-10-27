package com.evtnet.evtnetback.repository;

import java.util.List;
import java.util.Optional;

import com.evtnet.evtnetback.entity.TipoUsuarioGrupo;

public interface TipoUsuarioGrupoRepository extends BaseRepository <TipoUsuarioGrupo, Long> {
     Optional<TipoUsuarioGrupo> findById(Long id);
     Optional<TipoUsuarioGrupo> findByNombreIgnoreCase(String nombre);
     List<TipoUsuarioGrupo> findByIdIn(List<Long> ids);
}
