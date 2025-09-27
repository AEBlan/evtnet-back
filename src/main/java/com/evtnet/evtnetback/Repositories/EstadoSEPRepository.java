package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.EstadoSEP;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface EstadoSEPRepository extends BaseRepository <EstadoSEP, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE EstadoSEP me SET me.nombre = :nombre, me.descripcion = :descripcion WHERE me.id = :id")
    void update(@Param("id") Long id, @Param("nombre") String nombre, @Param("descripcion") String descripcion);

    @Modifying
    @Transactional
    @Query("UPDATE EstadoSEP me SET me.fechaHoraBaja = :fecha WHERE me.id = :id")
    void delete(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);
}
