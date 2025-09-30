package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.dto.reportes.DTOReportePersonsasEnEventosEnEspacio;

import com.evtnet.evtnetback.dto.reportes.DatoLocal;

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
            on i.fechaBaja is null
        where e.espacio.id = :espacioId
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
           e.espacio.id as espacioId,
           e.espacio.nombre as espacio,
           count(e.id) as eventos
        from Evento e
        where e.espacio.id in :espacios
          and e.fechaHoraInicio < :hasta
          and e.fechaHoraFin    > :desde
        group by e.espacio.id, e.espacio.nombre
        order by count(e.id) desc
    """)
    List<RowEventosPorEspacio> contarEventosPorEspacio(
            @Param("espacios") Collection<Long> espacios,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta
    );
}
