package com.evtnet.evtnetback.Repositories;

import org.springframework.stereotype.Repository;

import com.evtnet.evtnetback.Entities.AdministradorEvento;

@Repository
public interface AdministradorEventoRepository extends BaseRepository <AdministradorEvento, Long>{

    // Método corregido: buscar por el username del responsable
    // boolean existsByIdAndAdministradoresEventoUsuarioUsername(Long eventoId, String username);
    
}
