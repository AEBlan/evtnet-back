package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Inscripcion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InscripcionRepository extends BaseRepository<Inscripcion, Long> {
    int countByEventoId(Long eventoId);
    int countByEventoIdAndUsuarioUsername(Long eventoId, String username);
    Optional<Inscripcion> findByEventoIdAndUsuarioUsername(Long eventoId, String username);

    @Query("""
        select coalesce(sum(1 + size(i.invitados)), 0)
        from Inscripcion i
        where i.evento.id = :eventoId
    """)
    int countParticipantesEfectivos(@Param("eventoId") Long eventoId);

    @Query("""
        select coalesce(max(size(i.invitados)), 0)
        from Inscripcion i
        where i.evento.id = :eventoId
    """)
    int maxInvitadosPorInscripcionVigente(@Param("eventoId") Long eventoId);
}

