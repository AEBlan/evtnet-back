package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.ImagenEspacio;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ImagenEspacioRepository extends BaseRepository<ImagenEspacio, Long> {
    Optional<ImagenEspacio> findTopByEspacio_IdOrderByOrdenDesc(Long espacioId);

    @Query("""
    SELECT i
    FROM ImagenEspacio i
    WHERE i.espacio.id = :idEspacio
        AND i.fechaHoraBaja is null
   ORDER BY i.orden
   """)
    List<ImagenEspacio> findByEspacio_IdOrderByOrdenAsc(@Param("idEspacio") Long idEspacio);

    @Query("""
    SELECT i
    FROM ImagenEspacio i
    WHERE i.espacio.id = :idEspacio
        AND i.orden = :orden
        AND i.fechaHoraBaja is null
    """)
    ImagenEspacio findByEspacioYOrden(@Param("idEspacio")Long idEspacio, @Param("orden")int orden);
}

