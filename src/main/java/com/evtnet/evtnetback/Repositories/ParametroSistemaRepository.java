package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.ParametroSistema;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ParametroSistemaRepository extends BaseRepository <ParametroSistema, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE ParametroSistema ps SET ps.nombre = :nombre, ps.valor = :valor WHERE ps.id = :id")
    void update(@Param("id") Long id, @Param("nombre") String nombre, @Param("valor") String valor);

    @Modifying
    @Transactional
    @Query("UPDATE ParametroSistema ps SET ps.fechaHoraBaja = :fecha WHERE ps.id = :id")
    void delete(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);

    ParametroSistema findByNombre(String nombre);
    Optional<ParametroSistema> findByIdentificador(String identificador);
}
