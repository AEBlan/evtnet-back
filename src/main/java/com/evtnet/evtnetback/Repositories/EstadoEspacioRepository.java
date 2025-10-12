package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.EstadoEspacio;
import com.evtnet.evtnetback.dto.espacios.DTOEspacioEstado;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstadoEspacioRepository extends BaseRepository<EstadoEspacio, Long> {
    Optional<EstadoEspacio> findByNombre(String nombre);

    @Query("""
        SELECT estadoEspacio.nombre, espacioEstado.descripcion
        FROM EspacioEstado espacioEstado
        JOIN espacioEstado.estadoEspacio estadoEspacio
        WHERE espacioEstado.espacio.id = :idEspacio
            AND espacioEstado.fechaHoraBaja is not null
    """)
    DTOEspacioEstado espacioEstadoByEspacio(@Param("idEspacio") Long idEspacio);

}
