package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.ComisionPorOrganizacion;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface ComisionPorOrganizacionRepository extends BaseRepository <ComisionPorOrganizacion, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE ComisionPorOrganizacion cpo SET cpo.montoLimite = :montoLimite, cpo.porcentaje = :porcentaje, cpo.fechaDesde = :fechaDesde, cpo.fechaHasta = :fechaHasta WHERE cpo.id = :id")
    void update(@Param("id") Long id, @Param("montoLimite") BigDecimal montoLimite, @Param("porcentaje") BigDecimal porcentaje, @Param("fechaDesde") LocalDateTime fechaDesde, @Param("fechaHasta") LocalDateTime fechaHasta);

    @Modifying
    @Transactional
    @Query("UPDATE ComisionPorOrganizacion cpo SET cpo.fechaHasta = :fecha WHERE cpo.id = :id")
    void delete(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);
}
