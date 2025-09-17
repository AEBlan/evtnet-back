package com.evtnet.evtnetback.Repositories;

import java.util.List;
import java.util.Optional;

import com.evtnet.evtnetback.Entities.TipoUsuarioGrupo;

import com.evtnet.evtnetback.Repositories.BaseRepository;

public interface TipoUsuarioGrupoRepository extends BaseRepository <TipoUsuarioGrupo, Long> {
     Optional<TipoUsuarioGrupo> findById(Long id);
     Optional<TipoUsuarioGrupo> findByNombreIgnoreCase(String nombre);
     List<TipoUsuarioGrupo> findByIdIn(List<Long> ids);
}
