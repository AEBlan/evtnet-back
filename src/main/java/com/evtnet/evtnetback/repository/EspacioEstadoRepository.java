package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.EspacioEstado;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EspacioEstadoRepository extends BaseRepository<EspacioEstado, Long>{
    @Query("""
        SELECT espacioEstado
        FROM EspacioEstado espacioEstado
        JOIN espacioEstado.estadoEspacio estadoEspacio
        WHERE espacioEstado.espacio.id = :idEspacio
            AND espacioEstado.fechaHoraBaja is null
    """)
    EspacioEstado findActualByEspacio(@Param("idEspacio") Long idEspacio);

}
