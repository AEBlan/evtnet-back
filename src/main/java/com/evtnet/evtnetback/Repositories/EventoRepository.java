// src/main/java/com/evtnet/evtnetback/Repositories/EventoRepository.java
package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Evento;

import com.evtnet.evtnetback.Repositories.BaseRepository;

import com.evtnet.evtnetback.dto.espacios.DTOResultadoBusquedaEventosPorEspacio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends BaseRepository<Evento, Long> {

    @Query("""
        select distinct e
        from Evento e
        left join fetch e.subEspacio sub
        left join fetch sub.espacio esp
        left join fetch e.superEvento se
        left join fetch e.disciplinasEvento de
        left join fetch de.disciplina d
        where e.id = :id
    """)
    Optional<Evento> findByIdForDetalle(@Param("id") long id);

    @Query("""
        select count(e)
        from Evento e
        where e.subEspacio.espacio.id = :idEspacio
        and e.fechaHoraInicio <= :hasta
        and e.fechaHoraFin >= :desde
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
    @Query("""
        select distinct e
        from Evento e
        join e.administradoresEvento ae
        join ae.tipoAdministradorEvento t
        where t.nombre = 'Organizador'
          and ae.usuario.username = :username
    """)
    List<Evento> findAllByOrganizador_Username(@Param("username") String username);

    @Query("""
        select distinct e
        from Evento e
        left join fetch e.subEspacio sub
        left join fetch sub.espacio esp
        left join fetch e.superEvento se
        left join fetch e.disciplinasEvento de
        left join fetch de.disciplina d
        left join fetch e.inscripciones i 
        left join fetch i.usuario u
        where (
            (e.fechaHoraInicio <= :hasta and e.fechaHoraFin >= :desde)
        )
    """)
    Optional<Evento> findByIdForDetalleSoloActivas(@Param("id") long id);

    @Query("""
    SELECT e
    FROM Evento e
    JOIN e.subEspacio sub
    JOIN sub.espacio esp
    LEFT JOIN e.disciplinasEvento de
    LEFT JOIN de.disciplina d
    WHERE esp.id = :idEspacio
    """)
    List<Evento> findAllByEspacio(@Param("idEspacio") Long idEspacio);

    @Query("""
    SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END
    FROM Evento e
    JOIN e.subEspacio s
    JOIN ConfiguracionHorarioEspacio c ON c.subEspacio = s
    JOIN c.horariosEspacio h
    WHERE h.id = :idHorario
      AND e.fechaHoraInicio BETWEEN c.fechaDesde AND c.fechaHasta
      AND (
        (FUNCTION('DAYNAME', e.fechaHoraInicio) = 'Monday' AND h.diaSemana = 'Lunes') OR
        (FUNCTION('DAYNAME', e.fechaHoraInicio) = 'Tuesday' AND h.diaSemana = 'Martes') OR
        (FUNCTION('DAYNAME', e.fechaHoraInicio) = 'Wednesday' AND h.diaSemana = 'Miércoles') OR
        (FUNCTION('DAYNAME', e.fechaHoraInicio) = 'Thursday' AND h.diaSemana = 'Jueves') OR
        (FUNCTION('DAYNAME', e.fechaHoraInicio) = 'Friday' AND h.diaSemana = 'Viernes') OR
        (FUNCTION('DAYNAME', e.fechaHoraInicio) = 'Saturday' AND h.diaSemana = 'Sábado') OR
        (FUNCTION('DAYNAME', e.fechaHoraInicio) = 'Sunday' AND h.diaSemana = 'Domingo')
      )
      AND FUNCTION('TIME', e.fechaHoraInicio) BETWEEN h.horaDesde AND h.horaHasta
""")
    boolean existenEventosByHorario(@Param("idHorario") Long idHorario);

    @Query("""
    SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END
    FROM Evento e
    JOIN e.subEspacio s
    JOIN ConfiguracionHorarioEspacio c ON c.subEspacio = s
    JOIN c.excepcionesHorarioEspacio ehe
    WHERE ehe.id = :idExcepcion
      AND e.fechaHoraInicio BETWEEN c.fechaDesde AND c.fechaHasta
      AND e.fechaHoraInicio BETWEEN ehe.fechaHoraDesde AND ehe.fechaHoraHasta
""")
    boolean existenEventosByExcepcion(@Param("idExcepcion") Long idExcepcion);

    @Query("""
    SELECT e
    FROM Evento e
    JOIN e.subEspacio se
    WHERE se.id=:idSubEspacio
        AND e.fechaHoraInicio BETWEEN :fechaDesde AND :fechaHasta
    """)
    List<Evento>findByFechas(@Param("idSubEspacio") Long idSubEspacio, @Param("fechaDesde") LocalDateTime fechaDesde, @Param("fechaHasta") LocalDateTime fechaHasta);

    @Query("""
    SELECT e
    FROM Evento e
    WHERE e.subEspacio.id = :idSubEspacio
      AND DATE(e.fechaHoraInicio) = :fechaEvento
    """)
    List<Evento> findBySubEspacioAndFecha(@Param("idSubEspacio") Long idSubEspacio, @Param("fechaEvento") LocalDate fechaEvento);
    @Query("""
        SELECT e
        FROM Evento e
        WHERE e.subEspacio.id = :idSubEspacio
          AND e.fechaHoraInicio < :fin
          AND e.fechaHoraFin > :inicio
    """)
    List<Evento> findEventosEnRango(@Param("idSubEspacio") Long idSubEspacio, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    @Query("""
    SELECT e
    FROM Evento e
    WHERE e.subEspacio.id = :idSubEspacio
    AND (
        (COALESCE(:desde1, null) IS NOT NULL AND e.fechaHoraInicio >= :desde1 AND e.fechaHoraInicio < :hasta1)
        OR (COALESCE(:desde2, null) IS NOT NULL AND e.fechaHoraInicio > :desde2 AND e.fechaHoraInicio <= :hasta2)
    )
""")
    List<Evento> findEventosFueraDeRango(
            @Param("idSubEspacio") Long idSubEspacio,
            @Param("desde1") LocalDateTime desde1,
            @Param("hasta1") LocalDateTime hasta1,
            @Param("desde2") LocalDateTime desde2,
            @Param("hasta2") LocalDateTime hasta2
    );

    @Query("""
    SELECT DISTINCT e
    FROM Evento e
    JOIN e.subEspacio sub
    JOIN sub.espacio esp
    WHERE esp.id = :idEspacio
      AND (LOWER(e.nombre) LIKE CONCAT('%', LOWER(:texto), '%')
           OR LOWER(e.descripcion) LIKE CONCAT('%', LOWER(:texto), '%'))
""")
    List<Evento> findEventosByTexto(@Param("idEspacio") Long idEspacio, @Param("texto") String texto);

    @Query("""
    SELECT DISTINCT e
    FROM Evento e
    JOIN e.subEspacio sub
    JOIN sub.espacio esp
    WHERE esp.id = :idEspacio
      AND e.fechaHoraInicio BETWEEN :fechaHoraInicio AND :fechaHoraFin
""")
    List<Evento> findEventosByFecha(@Param("idEspacio") Long idEspacio,
                                    @Param("fechaHoraInicio") LocalDateTime fechaHoraInicio,
                                    @Param("fechaHoraFin") LocalDateTime fechaHoraFin);

    @Query("""
    SELECT DISTINCT e
    FROM Evento e
    JOIN e.subEspacio sub
    JOIN sub.espacio esp
    WHERE esp.id = :idEspacio
      AND e.precioInscripcion <= :precioLimite
""")
    List<Evento> findEventosByPrecio(@Param("idEspacio") Long idEspacio,
                                     @Param("precioLimite") BigDecimal precioLimite);

    @Query("""
    SELECT DISTINCT e
    FROM Evento e
    JOIN e.subEspacio sub
    JOIN sub.espacio esp
    LEFT JOIN e.disciplinasEvento de
    LEFT JOIN de.disciplina d
    WHERE esp.id = :idEspacio
      AND d.id IN :disciplinas
""")
    List<Evento> findEventosByDisciplinas(@Param("idEspacio") Long idEspacio,
                                          @Param("disciplinas") List<Long> disciplinas);

}
