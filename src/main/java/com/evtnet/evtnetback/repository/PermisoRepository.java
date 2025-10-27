package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.Permiso;
import java.util.Optional;
import java.util.List;

public interface PermisoRepository extends BaseRepository <Permiso, Long> {
    Optional<Permiso> findByNombreIgnoreCase(String nombre);
    List<Permiso> findAllByOrderByNombreAsc();
}
