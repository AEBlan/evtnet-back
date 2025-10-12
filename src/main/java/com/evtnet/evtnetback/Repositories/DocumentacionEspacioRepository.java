package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.DocumentacionEspacio;
import com.evtnet.evtnetback.Entities.EspacioEstado;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentacionEspacioRepository extends BaseRepository<DocumentacionEspacio, Long>{
    @Query("""
        SELECT de
        FROM DocumentacionEspacio de
        WHERE de.espacio.id = :idEspacio
            AND de.fechaHoraBaja is not null
    """)
    List<DocumentacionEspacio> findByEspacioId(@Param("idEspacio") Long idEspacio);
}
