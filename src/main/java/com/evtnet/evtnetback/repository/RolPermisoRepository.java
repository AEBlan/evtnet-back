package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.Rol;
import com.evtnet.evtnetback.entity.RolPermiso;
import java.util.List;

public interface RolPermisoRepository extends BaseRepository <RolPermiso, Long> {
    List<RolPermiso> findByRolIdOrderByFechaHoraAltaAsc(Long rolId);
    List<RolPermiso> findByRolAndFechaHoraBajaIsNull(Rol rol);
    boolean existsByRolIdAndPermisoNombreIgnoreCaseAndFechaHoraBajaIsNull(Long rolId, String permisoNombre);
}
