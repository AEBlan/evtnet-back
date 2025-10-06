package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Disciplina;
import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.Repositories.DisciplinaRepository;
import com.evtnet.evtnetback.dto.disciplinas.DTOBusquedaDisciplina;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinaRef;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
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
    public Page<DTODisciplinas> buscarDisciplinas(Pageable pageable, DTOBusquedaDisciplina filtros)throws Exception {
        Specification<Disciplina> spec = Specification.where(null);

        if (filtros != null) {
            if (filtros.getTexto() != null && !filtros.getTexto().isBlank()) {
                String q = "%" + filtros.getTexto().trim().toLowerCase() + "%";
                spec = spec.and((root, cq, cb) -> cb.or(
                        cb.like(cb.lower(root.get("nombre")), q),
                        cb.like(cb.lower(root.get("descripcion")), q)
                ));
            }
            if(filtros.getFechaDesde()!=null){
                LocalDateTime fechaDesde = Instant.ofEpochMilli(filtros.getFechaDesde())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                spec = spec.and((root, cq, cb) ->
                        cb.greaterThanOrEqualTo(root.get("fechaHoraAlta"), fechaDesde));
            }
            if(filtros.getFechaHasta()!=null){
                LocalDateTime fechaHasta = Instant.ofEpochMilli(filtros.getFechaHasta())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                spec = spec.and((root, cq, cb) ->
                        cb.greaterThanOrEqualTo(root.get("fechaHoraAlta"), fechaHasta));
            }
        }

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

    @Override
    public DTODisciplinas obtenerDisciplinaCompleta(Long id){
        Disciplina disciplina = disciplinaRepository.findById(id).get();
        return DTODisciplinas.builder()
                .id(disciplina.getId())
                .nombre(disciplina.getNombre())
                .descripcion(disciplina.getDescripcion())
                .fechaAlta(disciplina.getFechaHoraAlta() == null ? null
                        : disciplina.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .fechaBaja(disciplina.getFechaHoraAlta() == null ? null
                        : disciplina.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
    }

    @Override
    public void altaDisciplina(DTODisciplinas disciplina) throws Exception {
        this.save(Disciplina.builder()
                .nombre(disciplina.getNombre())
                .descripcion(disciplina.getDescripcion())
                .fechaHoraAlta(LocalDateTime.now())
                .build());
    }

    @Override
    public void modificarDisciplina(DTODisciplinas disciplina)throws Exception{
        disciplinaRepository.update(disciplina.getId(), disciplina.getNombre(), disciplina.getDescripcion());
    }

    @Override
    public void bajaDisciplina(Long id) throws Exception {
        disciplinaRepository.delete(id, LocalDateTime.now());
    }
}

