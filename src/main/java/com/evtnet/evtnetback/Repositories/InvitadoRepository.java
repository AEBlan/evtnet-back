// src/main/java/com/evtnet/evtnetback/Repositories/InvitadoRepository.java
package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Invitado;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InvitadoRepository extends BaseRepository<Invitado, Long> {
    @Modifying
    @Query("delete from Invitado inv where inv.inscripcion.id = :inscripcionId")
    void deleteByInscripcionId(Long inscripcionId);
}

