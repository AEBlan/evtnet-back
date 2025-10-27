package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.disciplinas.DTOBusquedaDisciplina;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinaRef;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import org.springframework.data.domain.*;

import java.util.List;

public interface DisciplinaService {
    List<DTODisciplinaRef> buscar(String text) throws Exception;
    List<DTODisciplinaRef> buscarPorSubEspacio(String texto, Long subEspacioId)throws Exception;
    Page<DTODisciplinas> buscarDisciplinas(Pageable pageable, DTOBusquedaDisciplina filtros) throws Exception;
    DTODisciplinas obtenerDisciplinaCompleta(Long id) throws Exception;
    void altaDisciplina(DTODisciplinas disciplina) throws Exception;
    void modificarDisciplina(DTODisciplinas disciplina) throws Exception;
    void bajaDisciplina(Long id) throws Exception;
}

