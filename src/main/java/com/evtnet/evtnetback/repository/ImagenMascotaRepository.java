package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.ImagenMascota;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ImagenMascotaRepository extends BaseRepository <ImagenMascota, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE ImagenMascota im SET im.imagen = :imagen WHERE im.id = :id")
    void update(@Param("id") Long id, @Param("imagen") String imagen);

    @Modifying
    @Transactional
    @Query("UPDATE ImagenMascota im SET im.fechaHoraBaja = :fecha WHERE im.id = :id")
    void delete(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);

    @Query("SELECT im FROM ImagenMascota im WHERE im.fechaHoraBaja IS NULL")
    List<ImagenMascota> findAllActivas();
}
