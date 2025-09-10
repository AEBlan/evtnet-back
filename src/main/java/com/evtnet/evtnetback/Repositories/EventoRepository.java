package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Evento;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EventoRepository extends BaseRepository<Evento, Long> {

    @Query("""
        SELECT e FROM Evento e
        LEFT JOIN FETCH e.disciplinasEvento de
        LEFT JOIN FETCH de.disciplina d
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
        select (count(e) > 0)
        from Evento e
        join e.administradoresEvento ae
        join ae.usuario u
        where e.id = :eventoId
          and u.username = :username
    """)
    boolean existsByEventoIdAndAdministradorUsername(@Param("eventoId") Long eventoId,
                                                     @Param("username") String username);
}
