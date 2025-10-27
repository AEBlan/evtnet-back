package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.EstadoEspacio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstadoEspacioRepository extends BaseRepository<EstadoEspacio, Long> {
    Optional<EstadoEspacio> findByNombre(String nombre);


    @Query("""
    SELECT t.estadoDestino
    FROM TransicionEstadoEspacio t
    WHERE t.estadoOrigen.id = :idEstadoOrigen
    """)
    List<EstadoEspacio>findDestinosByOrigen(@Param("idEstadoOrigen")Long idEstadoOrigen);
}
