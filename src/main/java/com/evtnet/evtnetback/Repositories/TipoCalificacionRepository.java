package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.TipoCalificacion;
import java.util.Optional;
import java.util.List;

public interface TipoCalificacionRepository extends BaseRepository <TipoCalificacion, Long> {
    Optional<TipoCalificacion> findByNombreIgnoreCase(String nombre);
    List<TipoCalificacion> findAllByOrderByNombreAsc();


}
