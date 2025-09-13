package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Services.DisciplinaService;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController {
    private final DisciplinaService service;
    public DisciplinaController(DisciplinaService service) { this.service = service; }

    @GetMapping("/buscar")
    public ResponseEntity<List<DTODisciplinas>> buscar(@RequestParam String text) {
        try { return ResponseEntity.ok(service.buscar(text)); }
        catch (Exception e) { return ResponseEntity.badRequest().build(); }
    }

    @GetMapping("/buscarPorEspacio")
    public ResponseEntity<List<DTODisciplinas>> buscarPorEspacio(
            @RequestParam String text, @RequestParam Long espacioId) {
        try { return ResponseEntity.ok(service.buscarPorEspacio(text, espacioId)); }
        catch (Exception e) { return ResponseEntity.badRequest().build(); }
    }
}

