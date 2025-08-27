package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Rol;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends BaseRepository <Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
    
}
