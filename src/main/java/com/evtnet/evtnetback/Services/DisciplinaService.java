package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinaRef;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import org.springframework.data.domain.*;

import java.util.List;

public interface DisciplinaService {
    List<DTODisciplinaRef> buscar(String text) throws Exception;
    List<DTODisciplinaRef> buscarPorEspacio(String text, Long espacioId) throws Exception;
    Page<DTODisciplinas> listaDisciplinas(Pageable pageable) throws Exception;
}

