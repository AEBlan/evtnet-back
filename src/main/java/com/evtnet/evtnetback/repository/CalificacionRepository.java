package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.Calificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
          and c.calificacionTipo.nombre = 'Calificación'
        group by lower(t.nombre)
        """)
    List<Object[]> conteoPorTipo(@Param("username") String username);


    @Query("SELECT c FROM Calificacion c JOIN c.calificacionTipo ct WHERE ct.nombre LIKE 'Denuncia'")
    Page<Calificacion> obtenerDenuncias(Pageable pageable);
}
