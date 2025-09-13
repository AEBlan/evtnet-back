package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import java.util.List;

public interface DisciplinaService {
    List<DTODisciplinas> buscar(String text) throws Exception;
    List<DTODisciplinas> buscarPorEspacio(String text, Long espacioId) throws Exception;
}

