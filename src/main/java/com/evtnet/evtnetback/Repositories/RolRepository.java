package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Rol;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RolRepository extends BaseRepository <Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
    List<Rol> findByFechaHoraBajaIsNullOrderByNombreAsc();
    // Para validar duplicados
    boolean existsByNombreIgnoreCaseAndFechaHoraBajaIsNull(String nombre);
    Optional<Rol> findByNombreIgnoreCase(String nombre);

}
