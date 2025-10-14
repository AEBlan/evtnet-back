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

    // ==========================================================
    //  Personas por Evento dentro de un Espacio
    // ==========================================================
    @Query("""
        SELECT new com.evtnet.evtnetback.dto.reportes.DatoLocal(
            e.nombre,
            e.fechaHoraInicio,
            e.fechaHoraFin,
            COUNT(DISTINCT i.id)
        )
        FROM Evento e
        LEFT JOIN e.inscripciones i
            ON i.fechaHoraBaja IS NULL
        JOIN e.subEspacio sub
        JOIN sub.espacio esp
        WHERE esp.id = :espacioId
          AND e.fechaHoraInicio < :hasta
          AND e.fechaHoraFin > :desde
        GROUP BY e.id, e.nombre, e.fechaHoraInicio, e.fechaHoraFin
        ORDER BY COUNT(DISTINCT i.id) DESC
    """)
    List<DatoLocal> reportePersonasPorEventoEnEspacio(
            @Param("espacioId") Long espacioId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );


    // ==========================================================
    //  Contar Eventos por Espacio (suma de todos sus subespacios)
    // ==========================================================
    interface RowEventosPorEspacio {
        Long getEspacioId();
        String getEspacio();
        long getEventos();
    }

    @Query("""
        SELECT
            esp.id AS espacioId,
            esp.nombre AS espacio,
            COUNT(DISTINCT e.id) AS eventos
        FROM Evento e
        JOIN e.subEspacio sub
        JOIN sub.espacio esp
        WHERE esp.id IN :espacios
          AND e.fechaHoraInicio < :hasta
          AND e.fechaHoraFin > :desde
        GROUP BY esp.id, esp.nombre
        ORDER BY COUNT(DISTINCT e.id) DESC
    """)
    List<RowEventosPorEspacio> contarEventosPorEspacio(
            @Param("espacios") Collection<Long> espacios,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );


    // ==========================================================
    //  Contar Participantes en un rango (por espacio o todos)
    // ==========================================================
    @Query("""
        SELECT COUNT(i)
        FROM Inscripcion i
        JOIN i.evento ev
        JOIN ev.subEspacio sub
        JOIN sub.espacio esp
        WHERE (:espacioId IS NULL OR esp.id = :espacioId)
          AND ev.fechaHoraInicio < :hasta
          AND ev.fechaHoraFin > :desde
          AND i.fechaHoraBaja IS NULL
    """)
    long contarParticipantesPorRango(
            @Param("espacioId") Long espacioId, // null si es "todos"
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}
