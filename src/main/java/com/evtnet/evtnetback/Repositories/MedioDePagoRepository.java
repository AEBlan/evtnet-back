package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.MedioDePago;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

public interface MedioDePagoRepository extends BaseRepository <MedioDePago, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE MedioDePago mp SET mp.icono = :imagen, mp.nombre = :nombre WHERE mp.id = :id")
    void update(@Param("id") Long id, @Param("imagen") String imagen, @Param("nombre") String nombre);

    @Modifying
    @Transactional
    @Query("UPDATE MedioDePago mp SET mp.fechaHoraBaja = :fecha WHERE mp.id = :id")
    void delete(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);
}
