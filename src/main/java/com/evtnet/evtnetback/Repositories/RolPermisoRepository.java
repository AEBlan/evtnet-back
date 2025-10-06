package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Rol;
import com.evtnet.evtnetback.Entities.RolPermiso;
import java.util.List;

public interface RolPermisoRepository extends BaseRepository <RolPermiso, Long> {
    List<RolPermiso> findByRolIdOrderByFechaHoraAltaAsc(Long rolId);
    List<RolPermiso> findByRolAndFechaHoraBajaIsNull(Rol rol);
    boolean existsByRolIdAndPermisoNombreIgnoreCaseAndFechaHoraBajaIsNull(Long rolId, String permisoNombre);
}
