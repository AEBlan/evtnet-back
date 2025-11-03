package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.SEPEstado;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evtnet.evtnetback.entity.EventoEstado;

@Repository
public interface EventoEstadoRepository extends BaseRepository <EventoEstado, Long>{
    @Query("""
    SELECT ee
    FROM EventoEstado ee
    WHERE ee.evento.id = :idEvento
      AND ee.fechaHoraBaja is null
    ORDER BY ee.id DESC
""")
    EventoEstado findUltimoByEvento(@Param("idEvento") Long idEvento);
    
}
