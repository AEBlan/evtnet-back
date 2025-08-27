package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Evento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventoRepository extends BaseRepository<Evento, Long> {

    @Query("""
        SELECT e FROM Evento e
        LEFT JOIN FETCH e.disciplinasEvento de
        LEFT JOIN FETCH e.modoEvento me
        LEFT JOIN FETCH e.eventosModoEvento eme
        LEFT JOIN FETCH e.inscripciones ins
        LEFT JOIN FETCH ins.usuario u
        LEFT JOIN FETCH e.superEvento se
        LEFT JOIN FETCH e.espacio esp
        WHERE e.id = :id
    """)
    Optional<Evento> findByIdFetchAll(long id);

    @Query("""
        SELECT COUNT(e) FROM Evento e
        WHERE e.espacio.id = :idEspacio
          AND e.fechaHoraInicio <= :hasta
          AND e.fechaHoraFin >= :desde
    """)
    int contarSuperpuestosPorEspacio(long idEspacio, LocalDateTime desde, LocalDateTime hasta);

    @Query("""
        SELECT COUNT(e) > 0 FROM Evento e
        WHERE e.id = :eventoId
          AND e.administradorEvento.responsable.username = :username
    """)
    boolean existsByEventoIdAndAdministradorUsername(Long eventoId, String username);
}

