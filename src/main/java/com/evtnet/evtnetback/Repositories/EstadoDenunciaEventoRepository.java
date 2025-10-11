package com.evtnet.evtnetback.Repositories;

import org.springframework.stereotype.Repository;

import com.evtnet.evtnetback.Entities.EstadoDenunciaEvento;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
}
