package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.dto.reportes.DTOReportePersonsasEnEventosEnEspacio;

import com.evtnet.evtnetback.dto.reportes.DatoLocal;

import com.evtnet.evtnetback.Repositories.ReporteRepository.RowEventosPorEspacio;

import com.evtnet.evtnetback.Entities.Evento;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ReporteRepository extends JpaRepository<Evento, Long> {

        @Query("""
        select new com.evtnet.evtnetback.dto.reportes.DatoLocal(
            e.nombre,
            e.fechaHoraInicio,
            e.fechaHoraFin,
            count(distinct i.id)
        )
        from Evento e
        left join e.inscripciones i
            on i.fechaHoraBaja is null
        join e.subEspacio sub
        join sub.espacio esp
        where esp.id = :espacioId
        and e.fechaHoraInicio < :hasta
        and e.fechaHoraFin    > :desde
        group by e.id, e.nombre, e.fechaHoraInicio, e.fechaHoraFin
        order by count(distinct i.id) desc
    """)
    List<DatoLocal> reportePersonasPorEventoEnEspacio(
            @Param("espacioId") Long espacioId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
    interface RowEventosPorEspacio {
        Long getEspacioId();
        String getEspacio();
        long getEventos();
    }

    @Query("""
        select
            esp.id as espacioId,
            esp.nombre as espacio,
            count(e.id) as eventos
        from Evento e
        join e.subEspacio sub
        join sub.espacio esp
        where esp.id in :espacios
        and e.fechaHoraInicio < :hasta
        and e.fechaHoraFin > :desde
        group by esp.id, esp.nombre
        order by count(e.id) desc
    """)
    List<RowEventosPorEspacio> contarEventosPorEspacio(
            @Param("espacios") Collection<Long> espacios,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );

    @Query("""
        SELECT COUNT(i)
        FROM Inscripcion i
        JOIN i.evento ev
        JOIN ev.subEspacio sub
        JOIN sub.espacio esp
        WHERE (:espacioId IS NULL OR esp.id = :espacioId)
        AND ev.fechaHoraInicio >= :desde
        AND ev.fechaHoraFin <= :hasta
        AND i.fechaHoraBaja IS NULL
    """)
    long contarParticipantesPorRango(
            @Param("espacioId") Long espacioId, // null si es "todos"
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}
