package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.ComisionPorInscripcion;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface ComisionPorInscripcionRepository extends BaseRepository <ComisionPorInscripcion, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE ComisionPorInscripcion cpi SET cpi.montoLimite = :montoLimite, cpi.porcentaje = :porcentaje, cpi.fechaDesde = :fechaDesde, cpi.fechaHasta = :fechaHasta WHERE cpi.id = :id")
    void update(@Param("id") Long id, @Param("montoLimite")BigDecimal montoLimite, @Param("porcentaje") BigDecimal porcentaje, @Param("fechaDesde") LocalDateTime fechaDesde, @Param("fechaHasta") LocalDateTime fechaHasta);

    @Modifying
    @Transactional
    @Query("UPDATE ComisionPorInscripcion cpi SET cpi.fechaHasta = :fecha WHERE cpi.id = :id")
    void delete(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);
}
