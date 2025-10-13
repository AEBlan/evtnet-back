package com.evtnet.evtnetback.Repositories;

import com.evtnet.evtnetback.Entities.DisciplinaSubEspacio;

import com.evtnet.evtnetback.Repositories.BaseRepository;

import com.evtnet.evtnetback.Entities.SubEspacio;

import com.evtnet.evtnetback.Entities.Disciplina;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface DisciplinaSubEspacioRepository extends BaseRepository <DisciplinaSubEspacio, Long> {
    List<DisciplinaSubEspacio> findBySubEspacio(SubEspacio subEspacio);
    boolean existsBySubEspacioIdAndDisciplinaId(Long subEspacioId, Long disciplinaId);

}
