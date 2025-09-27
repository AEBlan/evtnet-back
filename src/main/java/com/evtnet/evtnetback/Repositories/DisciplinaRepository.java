package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.Disciplina;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface DisciplinaRepository extends BaseRepository <Disciplina, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Disciplina d SET d.nombre = :nombre, d.descripcion = :descripcion WHERE d.id = :id")
    void update(@Param("id") Long id, @Param("nombre") String nombre, @Param("descripcion") String descripcion);

    @Modifying
    @Transactional
    @Query("UPDATE Disciplina d SET d.fechaHoraBaja = :fecha WHERE d.id = :id")
    void delete(@Param("id") Long id, @Param("fecha") LocalDateTime fecha);

}
