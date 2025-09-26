package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Disciplina;
import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.Repositories.DisciplinaRepository;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinaRef;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
public class DisciplinaServiceImpl extends BaseServiceImpl<Disciplina, Long> implements DisciplinaService {
    private final DisciplinaRepository disciplinaRepository;
    public DisciplinaServiceImpl(DisciplinaRepository disciplinaRepository, BaseRepository<Disciplina, Long> repo) {
        super(repo);
        this.disciplinaRepository=disciplinaRepository;
    }

    @Override
    public List<DTODisciplinaRef> buscar(String text) throws Exception {
        String q = text == null ? "" : text.trim().toLowerCase();
        return findAll().stream()
                .filter(d -> d.getNombre() != null)
                .filter(d -> q.isEmpty() || d.getNombre().toLowerCase().contains(q))
                .map(d -> new DTODisciplinaRef(d.getId(), d.getNombre()))
                .toList();
    }

    @Override
    public List<DTODisciplinaRef> buscarPorEspacio(String text, Long espacioId) throws Exception {
        String q = text == null ? "" : text.trim().toLowerCase();
        return findAll().stream()
                .filter(d -> d.getDisciplinasEspacio() != null)
                .filter(d -> d.getDisciplinasEspacio().stream()
                              .anyMatch(de -> de.getEspacio() != null &&
                                              Objects.equals(de.getEspacio().getId(), espacioId)))
                .filter(d -> d.getNombre() != null &&
                             (q.isEmpty() || d.getNombre().toLowerCase().contains(q)))
                .map(d -> new DTODisciplinaRef(d.getId(), d.getNombre()))
                .toList();
    }

    @Override
    public Page<DTODisciplinas> listaDisciplinas(Pageable pageable)throws Exception {
        Specification<Disciplina> spec = Specification.where(null);
        Page<Disciplina> disciplinas = disciplinaRepository.findAll(spec, pageable);
        return disciplinas
                .map(d-> DTODisciplinas.builder()
                        .id(d.getId())
                        .nombre(d.getNombre())
                        .descripcion(d.getDescripcion())
                        .fechaAlta(d.getFechaHoraAlta() == null ? null
                                : d.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .fechaBaja(d.getFechaHoraBaja()==null ? null
                                :d.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .build()
            );
    }
}

