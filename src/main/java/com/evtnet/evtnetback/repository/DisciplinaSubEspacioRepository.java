package com.evtnet.evtnetback.repository;

import com.evtnet.evtnetback.entity.DisciplinaSubEspacio;

import com.evtnet.evtnetback.entity.SubEspacio;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface DisciplinaSubEspacioRepository extends BaseRepository <DisciplinaSubEspacio, Long> {
    List<DisciplinaSubEspacio> findBySubEspacio(SubEspacio subEspacio);
    @Query("""
        SELECT d.nombre
        FROM DisciplinaSubEspacio ds
        JOIN ds.disciplina d
        JOIN ds.subEspacio se
        WHERE se.id = :idSubEspacio
    """)
    List<String>disciplinasNombre(@Param("idSubEspacio") Long idSubEspacio);
    @Query("""
        SELECT ds
        FROM DisciplinaSubEspacio ds
        JOIN ds.disciplina d
        WHERE ds.subEspacio.id = :idSubEspacio
    """)
    List<DisciplinaSubEspacio>findAllBySubespacio(@Param("idSubEspacio") Long idSubEspacio);
    boolean existsBySubEspacioIdAndDisciplinaId(Long subEspacioId, Long disciplinaId);

}
