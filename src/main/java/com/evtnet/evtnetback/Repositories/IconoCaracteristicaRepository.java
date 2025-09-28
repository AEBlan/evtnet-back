package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.IconoCaracteristica;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface IconoCaracteristicaRepository extends BaseRepository <IconoCaracteristica, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE IconoCaracteristica ic SET ic.imagen = :imagen WHERE ic.id = :id")
    void update(@Param("id") Long id, @Param("imagen") String imagen);

    @Modifying
    @Transactional
    @Query("UPDATE IconoCaracteristica ic SET ic.fechaHoraBaja = :fecha WHERE ic.id = :id")
    void delete(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);
}
