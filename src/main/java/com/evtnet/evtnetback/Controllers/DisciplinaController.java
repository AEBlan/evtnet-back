package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.Disciplina;
import com.evtnet.evtnetback.Entities.DisciplinaEspacio;
import com.evtnet.evtnetback.Services.DisciplinaEspacioServiceImpl;
import com.evtnet.evtnetback.Services.DisciplinaServiceImpl;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController {

    private final DisciplinaServiceImpl disciplinaService;
    private final DisciplinaEspacioServiceImpl disciplinaEspacioService;

    public DisciplinaController(DisciplinaServiceImpl disciplinaService,
                                DisciplinaEspacioServiceImpl disciplinaEspacioService) {
        this.disciplinaService = disciplinaService;
        this.disciplinaEspacioService = disciplinaEspacioService;
    }

    // GET /disciplinas/buscar?text=...
    @GetMapping("/buscar")
    public ResponseEntity<List<DTODisciplinas>> buscar(@RequestParam String text) {
        try {
            final String q = text == null ? "" : text.trim().toLowerCase();

            List<DTODisciplinas> result = disciplinaService.findAll().stream()
                    .filter(d -> d.getNombre() != null &&
                                 (q.isEmpty() || d.getNombre().toLowerCase().contains(q)))
                    .map(this::toDto)
                    .toList();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /disciplinas/buscarPorEspacio?text=...&espacioId=...
    @GetMapping("/buscarPorEspacio")
    public ResponseEntity<List<DTODisciplinas>> buscarPorEspacio(@RequestParam String text,
                                                                @RequestParam Long espacioId) {
        try {
            final String q = text == null ? "" : text.trim().toLowerCase();

            // Relaciones Disciplina-Espacio
            List<DisciplinaEspacio> links = disciplinaEspacioService.findAll();

            // Disciplinas únicas vinculadas al espacio solicitado
            Map<Long, Disciplina> porId = links.stream()
                    .filter(link -> link.getEspacio() != null &&
                                    Objects.equals(link.getEspacio().getId(), espacioId))
                    .map(DisciplinaEspacio::getDisciplina)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            Disciplina::getId, d -> d, (a, b) -> a, LinkedHashMap::new));

            List<DTODisciplinas> result = porId.values().stream()
                    .filter(d -> d.getNombre() != null &&
                                 (q.isEmpty() || d.getNombre().toLowerCase().contains(q)))
                    .map(this::toDto)
                    .toList();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private DTODisciplinas toDto(Disciplina d) {
        return new DTODisciplinas(d.getId(), d.getNombre());
    }
}
