package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.MotivoCalificacion;
import com.evtnet.evtnetback.entity.TipoCalificacion;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MotivoCalificacionRepository extends BaseRepository <MotivoCalificacion, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE MotivoCalificacion mc SET mc.tipoCalificacion = :tipoCalificacion, mc.nombre = :nombre WHERE mc.id = :id")
    void update(@Param("id") Long id, @Param("tipoCalificacion") TipoCalificacion tipoCalificacion, @Param("nombre") String nombre);

    @Modifying
    @Transactional
    @Query("UPDATE MotivoCalificacion mc SET mc.fechaHoraBaja = :fecha WHERE mc.id = :id")
    void delete(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);

    List<MotivoCalificacion> findByTipoCalificacionId(Long tipoId);

    boolean existsByNombreAndTipoCalificacionId(String nombre, Long tipoId);
    List<MotivoCalificacion> findByTipoCalificacionIdAndFechaHoraBajaIsNull(Long tipoId);
}
