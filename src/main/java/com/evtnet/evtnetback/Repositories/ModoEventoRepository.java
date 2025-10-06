package com.evtnet.evtnetback.Repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evtnet.evtnetback.Entities.ModoEvento;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface ModoEventoRepository extends BaseRepository <ModoEvento, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE ModoEvento me SET me.nombre = :nombre, me.descripcion = :descripcion WHERE me.id = :id")
    void update(@Param("id") Long id, @Param("nombre") String nombre, @Param("descripcion") String descripcion);

    @Modifying
    @Transactional
    @Query("UPDATE ModoEvento me SET me.fechaHoraBaja = :fecha WHERE me.id = :id")
    void delete(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);
}
