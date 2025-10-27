package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.TipoCalificacion;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

@Repository
public interface TipoCalificacionRepository extends BaseRepository <TipoCalificacion, Long> {
    Optional<TipoCalificacion> findByNombreIgnoreCase(String nombre);
    List<TipoCalificacion> findAllByOrderByNombreAsc();
    @Modifying
    @Transactional
    @Query("UPDATE TipoCalificacion tc SET tc.imagen = :imagen, tc.nombre = :nombre WHERE tc.id = :id")
    void update(@Param("id") Long id, @Param("imagen") String imagen, @Param("nombre") String nombre);

    @Modifying
    @Transactional
    @Query("UPDATE TipoCalificacion tc SET tc.fechaHoraBaja = :fecha WHERE tc.id = :id")
    void delete(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);

}
