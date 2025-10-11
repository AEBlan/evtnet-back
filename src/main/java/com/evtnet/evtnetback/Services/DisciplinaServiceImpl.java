package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Disciplina;
import com.evtnet.evtnetback.Repositories.DisciplinaRepository;
import com.evtnet.evtnetback.Repositories.DisciplinaSubEspacioRepository;
import com.evtnet.evtnetback.dto.disciplinas.DTOBusquedaDisciplina;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinaRef;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;

import com.evtnet.evtnetback.Repositories.SubEspacioRepository;

import com.evtnet.evtnetback.Entities.DisciplinaSubEspacio;
import com.evtnet.evtnetback.Entities.SubEspacio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DisciplinaServiceImpl extends BaseServiceImpl<Disciplina, Long> implements DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaSubEspacioRepository disciplinaSubEspacioRepository;
    private final SubEspacioRepository subEspacioRepository;

    public DisciplinaServiceImpl(
            DisciplinaRepository disciplinaRepository,
            DisciplinaSubEspacioRepository disciplinaSubEspacioRepository,
            SubEspacioRepository subEspacioRepository
    ) {
        super(disciplinaRepository);
        this.disciplinaRepository = disciplinaRepository;
        this.disciplinaSubEspacioRepository = disciplinaSubEspacioRepository;
        this.subEspacioRepository = subEspacioRepository;
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
    public List<DTODisciplinaRef> buscarPorSubEspacio(String text, Long subespacioId) throws Exception {
        if (subespacioId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar el subespacio");

        SubEspacio subespacio = subEspacioRepository.findById(subespacioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subespacio no encontrado"));

        List<DisciplinaSubEspacio> relaciones = disciplinaSubEspacioRepository.findBySubEspacio(subespacio);

        return relaciones.stream()
                .map(DisciplinaSubEspacio::getDisciplina)
                .filter(d -> text == null || text.isBlank()
                        || d.getNombre().toLowerCase().contains(text.toLowerCase())
                        || (d.getDescripcion() != null && d.getDescripcion().toLowerCase().contains(text.toLowerCase())))
                .map(d -> new DTODisciplinaRef(d.getId(), d.getNombre()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<DTODisciplinas> buscarDisciplinas(Pageable pageable, DTOBusquedaDisciplina filtros) throws Exception {
        Specification<Disciplina> spec = Specification.where(null);

        if (filtros != null) {
            if (filtros.getTexto() != null && !filtros.getTexto().isBlank()) {
                String q = "%" + filtros.getTexto().trim().toLowerCase() + "%";
                spec = spec.and((root, cq, cb) -> cb.or(
                        cb.like(cb.lower(root.get("nombre")), q),
                        cb.like(cb.lower(root.get("descripcion")), q)
                ));
            }
            if (filtros.getFechaDesde() != null) {
                LocalDateTime fechaDesde = Instant.ofEpochMilli(filtros.getFechaDesde())
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();
                spec = spec.and((root, cq, cb) ->
                        cb.greaterThanOrEqualTo(root.get("fechaHoraAlta"), fechaDesde));
            }
            if (filtros.getFechaHasta() != null) {
                LocalDateTime fechaHasta = Instant.ofEpochMilli(filtros.getFechaHasta())
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();
                spec = spec.and((root, cq, cb) ->
                        cb.lessThanOrEqualTo(root.get("fechaHoraAlta"), fechaHasta));
            }
        }

        Page<Disciplina> disciplinas = disciplinaRepository.findAll(spec, pageable);
        return disciplinas.map(d ->
                DTODisciplinas.builder()
                        .id(d.getId())
                        .nombre(d.getNombre())
                        .descripcion(d.getDescripcion())
                        .fechaAlta(d.getFechaHoraAlta() == null ? null :
                                d.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .fechaBaja(d.getFechaHoraBaja() == null ? null :
                                d.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                        .build()
        );
    }

    @Override
    public DTODisciplinas obtenerDisciplinaCompleta(Long id) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplina no encontrada"));

        return DTODisciplinas.builder()
                .id(disciplina.getId())
                .nombre(disciplina.getNombre())
                .descripcion(disciplina.getDescripcion())
                .fechaAlta(disciplina.getFechaHoraAlta() == null ? null :
                        disciplina.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .fechaBaja(disciplina.getFechaHoraBaja() == null ? null :
                        disciplina.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
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
    public void modificarDisciplina(DTODisciplinas disciplina) throws Exception {
        disciplinaRepository.update(disciplina.getId(), disciplina.getNombre(), disciplina.getDescripcion());
    }

    @Override
    public void bajaDisciplina(Long id) throws Exception {
        disciplinaRepository.delete(id, LocalDateTime.now());
    }
}
