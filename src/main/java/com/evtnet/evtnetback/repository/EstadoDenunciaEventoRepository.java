package com.evtnet.evtnetback.repository;

import org.springframework.stereotype.Repository;

import com.evtnet.evtnetback.entity.EstadoDenunciaEvento;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EstadoDenunciaEventoRepository extends BaseRepository <EstadoDenunciaEvento, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE EstadoDenunciaEvento ede SET ede.nombre = :nombre, ede.descripcion = :descripcion WHERE ede.id = :id")
    void update(@Param("id") Long id, @Param("nombre") String nombre, @Param("descripcion") String descripcion);

    @Modifying
    @Transactional
    @Query("UPDATE EstadoDenunciaEvento ede SET ede.fechaHoraBaja = :fecha WHERE ede.id = :id")
    void delete(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);

    @Query("""
            SELECT eDestino
            FROM EstadoDenunciaEvento eOrigen
                JOIN eOrigen.transicionesOrigen t
                JOIN t.estadoDestino eDestino
            WHERE eOrigen.id = :idEstado
                AND t.fechaHoraBaja IS NULL
                AND eDestino.fechaHoraBaja IS NULL
            """)
    List<EstadoDenunciaEvento> obtenerPosiblesTransiciones(Long idEstado);
}
