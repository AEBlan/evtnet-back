package com.evtnet.evtnetback.Repositories;

import org.springframework.stereotype.Repository;

import com.evtnet.evtnetback.Entities.Inscripcion;

@Repository
public interface InscripcionRepository extends BaseRepository <Inscripcion, Long> {

     long countByEventoIdAndUsuarioUsername(Long eventoId, String username);
}
