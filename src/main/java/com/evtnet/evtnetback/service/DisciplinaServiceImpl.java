package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Disciplina;
import com.evtnet.evtnetback.repository.DisciplinaRepository;
import com.evtnet.evtnetback.repository.DisciplinaSubEspacioRepository;
import com.evtnet.evtnetback.dto.disciplinas.DTOBusquedaDisciplina;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinaRef;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;

import com.evtnet.evtnetback.repository.SubEspacioRepository;

import com.evtnet.evtnetback.entity.DisciplinaSubEspacio;
import com.evtnet.evtnetback.entity.SubEspacio;

import com.evtnet.evtnetback.util.RegistroSingleton;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DisciplinaServiceImpl extends BaseServiceImpl<Disciplina, Long> implements DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final DisciplinaSubEspacioRepository disciplinaSubEspacioRepository;
    private final SubEspacioRepository subEspacioRepository;
    private final ParametroSistemaService parametroSistemaService;
    private final RegistroSingleton registroSingleton;

    public DisciplinaServiceImpl(
            DisciplinaRepository disciplinaRepository,
            DisciplinaSubEspacioRepository disciplinaSubEspacioRepository,
            SubEspacioRepository subEspacioRepository,
            ParametroSistemaService parametroSistemaService,
            RegistroSingleton registroSingleton
    ) {
        super(disciplinaRepository);
        this.disciplinaRepository = disciplinaRepository;
        this.disciplinaSubEspacioRepository = disciplinaSubEspacioRepository;
        this.subEspacioRepository = subEspacioRepository;
        this.parametroSistemaService = parametroSistemaService;
        this.registroSingleton = registroSingleton;
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
    public Page<DTODisciplinas> buscarDisciplinas(int page, DTOBusquedaDisciplina filtros) throws Exception {
        Integer longitudPagina = parametroSistemaService.getInt("longitudPagina", 20);
        Pageable pageable = PageRequest.of(
                page,
                longitudPagina,
                Sort.by(
                        Sort.Order.asc("fechaHoraBaja"),
                        Sort.Order.asc("fechaHoraAlta")
                )
        );
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
            boolean vigentes = Boolean.TRUE.equals(filtros.isVigentes());
            boolean dadasDeBaja = Boolean.TRUE.equals(filtros.isDadasDeBaja());

            if (vigentes && !dadasDeBaja) {
                spec = spec.and((root, cq, cb) -> cb.isNull(root.get("fechaHoraBaja")));
            }

            if (!vigentes && dadasDeBaja) {
                spec = spec.and((root, cq, cb) -> cb.isNotNull(root.get("fechaHoraBaja")));
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
        if(disciplinaRepository.existsByName(disciplina.getNombre())) throw new Exception("Ya hay una disciplina dada de alta con ese nombre");
        Disciplina disciplinaNueva=this.save(Disciplina.builder()
                .nombre(disciplina.getNombre())
                .descripcion(disciplina.getDescripcion())
                .fechaHoraAlta(LocalDateTime.now())
                .build());
        registroSingleton.write("Parametros", "disciplina", "creacion", "Disciplina de ID " + disciplinaNueva.getId() + " nombre" +disciplinaNueva.getNombre()+ "'");
    }

    @Override
    public void modificarDisciplina(DTODisciplinas disciplina) throws Exception {
        if(disciplinaRepository.existsByName(disciplina.getNombre())) throw new Exception("Ya hay una disciplina dada de alta con ese nombre");
        disciplinaRepository.update(disciplina.getId(), disciplina.getNombre(), disciplina.getDescripcion());
        registroSingleton.write("Parametros", "disciplina", "modificacion", "Disciplina de ID " + disciplina.getId() + " nombre" +disciplina.getNombre()+ "'");
    }

    @Override
    public void bajaDisciplina(Long id) throws Exception {
        disciplinaRepository.delete(id, LocalDateTime.now());
        registroSingleton.write("Parametros", "disciplina", "eliminacion", "Disciplina de ID " + id+ "'");
    }

    @Override
    public void restaurarDisciplina(Long id) throws Exception{
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disciplina no encontrada"));
        if(disciplinaRepository.existsByName(disciplina.getNombre())) throw new Exception("Ya hay una disciplina dada de alta con ese nombre");
        disciplina.setFechaHoraBaja(null);
        disciplina.setFechaHoraAlta(LocalDateTime.now());
        this.save(disciplina);
        registroSingleton.write("Parametros", "disciplina", "restauracion", "Disciplina de ID " + id+ "'");
    }
}
