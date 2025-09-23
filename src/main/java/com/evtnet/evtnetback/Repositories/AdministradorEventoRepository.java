package com.evtnet.evtnetback.Repositories;

import org.springframework.stereotype.Repository;

import com.evtnet.evtnetback.Entities.AdministradorEvento;
import java.util.Optional;

@Repository
public interface AdministradorEventoRepository extends BaseRepository <AdministradorEvento, Long>{

    Optional<AdministradorEvento> findByEventoIdAndUsuarioUsernameAndFechaHoraBajaIsNull(
        Long eventoId, String username
    );
    
}
