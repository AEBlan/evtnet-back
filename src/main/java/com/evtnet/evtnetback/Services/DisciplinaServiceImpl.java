package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Disciplina;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Service
public class DisciplinaServiceImpl
        extends BaseServiceImpl<Disciplina, Long>
        implements DisciplinaService {

    public DisciplinaServiceImpl(BaseRepository<Disciplina, Long> repo) { super(repo); }

    @Override
    public List<DTODisciplinas> buscar(String text) throws Exception {
        String q = text == null ? "" : text.trim().toLowerCase();
        return findAll().stream()
                .filter(d -> d.getNombre() != null)
                .filter(d -> q.isEmpty() || d.getNombre().toLowerCase().contains(q))
                .map(d -> new DTODisciplinas(d.getId(), d.getNombre()))
                .toList();
    }

    @Override
    public List<DTODisciplinas> buscarPorEspacio(String text, Long espacioId) throws Exception {
        String q = text == null ? "" : text.trim().toLowerCase();
        return findAll().stream()
                .filter(d -> d.getDisciplinasEspacio() != null)
                .filter(d -> d.getDisciplinasEspacio().stream()
                              .anyMatch(de -> de.getEspacio() != null &&
                                              Objects.equals(de.getEspacio().getId(), espacioId)))
                .filter(d -> d.getNombre() != null &&
                             (q.isEmpty() || d.getNombre().toLowerCase().contains(q)))
                .map(d -> new DTODisciplinas(d.getId(), d.getNombre()))
                .toList();
    }
}

