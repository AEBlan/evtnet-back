// src/main/java/com/evtnet/evtnetback/Repositories/EventoRepository.java
package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Evento;

import com.evtnet.evtnetback.Repositories.BaseRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends BaseRepository<Evento, Long> {

    @Query("""
        select distinct e
        from Evento e
        left join fetch e.espacio esp
        left join fetch e.superEvento se
        left join fetch e.disciplinasEvento de
        left join fetch de.disciplina d
        where e.id = :id
    """)
    Optional<Evento> findByIdForDetalle(@Param("id") long id);

    @Query("""
        select count(e)
        from Evento e
        where e.espacio.id = :idEspacio
          and e.fechaHoraInicio <= :hasta
          and e.fechaHoraFin    >= :desde
    """)
    int contarSuperpuestosPorEspacio(@Param("idEspacio") long idEspacio,
                                     @Param("desde") LocalDateTime desde,
                                     @Param("hasta") LocalDateTime hasta);

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
    List<Evento> findAllByOrganizador_Username(String username);

}
