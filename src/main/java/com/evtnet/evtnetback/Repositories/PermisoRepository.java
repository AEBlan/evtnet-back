package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Permiso;
import java.util.Optional;
import java.util.List;

public interface PermisoRepository extends BaseRepository <Permiso, Long> {
    Optional<Permiso> findByNombreIgnoreCase(String nombre);
    List<Permiso> findAllByOrderByNombreAsc();
}
