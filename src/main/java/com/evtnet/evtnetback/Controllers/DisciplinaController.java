package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Services.DisciplinaService;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinaRef;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController {
    private final DisciplinaService service;
    public DisciplinaController(DisciplinaService service) { this.service = service; }

    @GetMapping("/buscar")
    public ResponseEntity<List<DTODisciplinaRef>> buscar(@RequestParam String text) {
        try { return ResponseEntity.ok(service.buscar(text)); }
        catch (Exception e) { return ResponseEntity.badRequest().build(); }
    }

    @GetMapping("/buscarPorEspacio")
    public ResponseEntity<List<DTODisciplinaRef>> buscarPorEspacio(
            @RequestParam String text, @RequestParam Long espacioId) {
        try { return ResponseEntity.ok(service.buscarPorEspacio(text, espacioId)); }
        catch (Exception e) { return ResponseEntity.badRequest().build(); }
    }

    @GetMapping("/listaDisciplinas")
    public ResponseEntity<Page<DTODisciplinas>> listaDisciplinas(@RequestParam(name = "page", defaultValue = "0") int page) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                    page, 10, org.springframework.data.domain.Sort.by("id").ascending()
            );
            return ResponseEntity.ok(service.listaDisciplinas(pageable));
        }
        catch (Exception e) { return ResponseEntity.badRequest().build(); }
    }
}

