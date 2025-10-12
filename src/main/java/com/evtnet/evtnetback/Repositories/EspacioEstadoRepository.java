package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.EspacioEstado;
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
            AND espacioEstado.fechaHoraBaja is not null
    """)
    EspacioEstado findActualByEspacio(@Param("idEspacio") Long idEspacio);

}
