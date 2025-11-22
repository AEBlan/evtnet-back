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

    @Query(""" 
    SELECT c
    FROM ComisionPorOrganizacion c
    WHERE c.montoLimite <= :valor
    AND c.fechaDesde <= CURRENT_TIMESTAMP AND (c.fechaHasta IS NULL OR c.fechaHasta > CURRENT_TIMESTAMP)
    ORDER BY c.montoLimite DESC
    """)
    ComisionPorOrganizacion findComisionByValor(@Param("valor") double valor);

    @Query("""
       SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
       FROM ComisionPorOrganizacion c
       WHERE c.montoLimite = :montoLimite
         AND (c.fechaHasta IS NULL OR c.fechaHasta > :hoy)
         AND c.fechaDesde < :hoy
         AND (:id IS NULL OR c.id <> :id)
       """)
    boolean existsVigenteWithSameMonto(
            @Param("montoLimite") BigDecimal montoLimite,
            @Param("hoy") LocalDateTime hoy,
            @Param("id") Long id
    );



}
