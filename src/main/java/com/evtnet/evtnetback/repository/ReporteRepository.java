package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.dto.reportes.DatoLocal;

import com.evtnet.evtnetback.entity.Evento;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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
            COUNT(i.id)
        )
        FROM Evento e
        JOIN e.inscripciones i
        LEFT JOIN e.subEspacio s
        LEFT JOIN s.espacio esp
        WHERE (
            (:subespacioId IS NULL AND esp.id = :espacioId)
            OR (s.id = :subespacioId)
        )
        AND e.fechaHoraInicio >= :fechaDesde
        AND e.fechaHoraFin <= :fechaHasta
        GROUP BY e.id, e.nombre, e.fechaHoraInicio, e.fechaHoraFin
        ORDER BY COUNT(i.id) DESC
    """)
    List<DatoLocal> reportePersonasPorEventoEnEspacio(
            @Param("espacioId") Long espacioId,
            @Param("subespacioId") Long subespacioId,
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta);


    // ==========================================================
//  Contar Eventos por Subespacio (dentro de un Espacio)
// ==========================================================
    // ==========================================================
//  Contar Eventos por Subespacio dentro de varios Espacios
// ==========================================================
        public interface RowEventosPorSubespacio {
            Long getEspacioId();
            String getEspacio();
            Long getSubespacioId();
            String getSubespacio();
            long getEventos();
        }

        @Query("""
            SELECT
                esp.id AS espacioId,
                esp.nombre AS espacio,
                sub.id AS subespacioId,
                sub.nombre AS subespacio,
                COUNT(DISTINCT e.id) AS eventos
            FROM Evento e
            JOIN e.subEspacio sub
            JOIN sub.espacio esp
            WHERE esp.id IN :espaciosIds
            AND e.fechaHoraInicio >= :desde
            AND e.fechaHoraFin <= :hasta
            GROUP BY esp.id, esp.nombre, sub.id, sub.nombre
            ORDER BY esp.nombre ASC, COUNT(DISTINCT e.id) DESC
        """)
        List<RowEventosPorSubespacio> contarEventosPorSubespaciosDeEspacios(
                @Param("espaciosIds") Collection<Long> espaciosIds,
                @Param("desde") LocalDateTime desde,
                @Param("hasta") LocalDateTime hasta
        );





        @Query("""
            SELECT COUNT(i.id)
            FROM Inscripcion i
            JOIN i.evento e
            JOIN e.subEspacio s
            WHERE s.id = :subespacioId
            AND i.fechaHoraAlta BETWEEN :inicio AND :fin
        """)
        Long contarParticipantesEnRangoPorSubespacio(
                @Param("subespacioId") Long subespacioId,
                @Param("inicio") LocalDateTime inicio,
                @Param("fin") LocalDateTime fin
        );


    // Ingresos por INSCRIPCIÓN
    @Query("""
        SELECT COALESCE(SUM(i.montoUnitario * i.cantidad), 0)
        FROM ComprobantePago cp
            JOIN cp.items i
        WHERE cp.fechaHoraEmision BETWEEN :inicio AND :fin
          AND cp.inscripcion IS NOT NULL
    """)
    BigDecimal obtenerIngresosPorInscripcion(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );

    // Ingresos por ORGANIZACIÓN
    @Query("""
        SELECT COALESCE(SUM(i.montoUnitario * i.cantidad), 0)
        FROM ComprobantePago cp
            JOIN cp.items i
        WHERE cp.fechaHoraEmision BETWEEN :inicio AND :fin
          AND cp.evento IS NOT NULL
          AND cp.inscripcion IS NULL
    """)
    BigDecimal obtenerIngresosPorOrganizacion(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin
    );


    @Query("""
    SELECT COALESCE(SUM(
        e.precioOrganizacion +
        (
            e.precioInscripcion * e.adicionalPorInscripcion * 
            (SELECT COUNT(i2.id) FROM Inscripcion i2 WHERE i2.evento.id = e.id)
        )
    ), 0)
    FROM Evento e
    WHERE e.fechaHoraInicio BETWEEN :desde AND :hasta
    """)
    BigDecimal obtenerCuotaPorEspacio(LocalDateTime desde, LocalDateTime hasta);
    
    
}