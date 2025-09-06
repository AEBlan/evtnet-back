package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CalificacionRepository extends BaseRepository <Calificacion, Long>{
    @Query("""
           select ct.nombre, count(c)
           from Calificacion c
             join c.calificacionTipo ct
             join c.calificado u
           where u.username = :username
           group by ct.nombre
           """)
    List<Object[]> countByTipoForCalificado(@Param("username") String username);
}
