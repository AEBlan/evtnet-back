package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Calificacion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CalificacionRepository extends BaseRepository <Calificacion, Long>{
      @Query("""
        select lower(t.nombre), count(distinct c.id)
        from Calificacion c
            join c.motivos cmc
            join cmc.motivoCalificacion m
            join m.tipoCalificacion t
        where c.calificado.username = :username
          and (c.calificacionTipo.nombre = 'Calificacion Normal' or c.calificacionTipo is null)
        group by lower(t.nombre)
        """)
    List<Object[]> conteoPorTipo(@Param("username") String username);
}
