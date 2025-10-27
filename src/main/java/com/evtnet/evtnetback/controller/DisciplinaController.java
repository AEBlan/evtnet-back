package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Services.DisciplinaService;
import com.evtnet.evtnetback.dto.disciplinas.DTOBusquedaDisciplina;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinaRef;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import com.evtnet.evtnetback.error.HttpErrorException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/buscarPorSubespacio")
    public ResponseEntity<List<DTODisciplinaRef>> buscarPorSubespacio(
            @RequestParam String text,
            @RequestParam Long subespacioId) {
        try {
            return ResponseEntity.ok(service.buscarPorSubEspacio(text, subespacioId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping ("/buscarDisciplinas")
    public ResponseEntity buscarDisciplinas(@RequestParam(name = "page", defaultValue = "0") int page, @RequestBody DTOBusquedaDisciplina filtros) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                    page, 10, org.springframework.data.domain.Sort.by("id").ascending()
            );
            return ResponseEntity.ok(service.buscarDisciplinas(pageable, filtros));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener las disciplinas"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping ("/obtenerDisciplinaCompleta")
    public ResponseEntity obtenerDisciplinaCompleta(@RequestParam(name="id", required=true) Long id) {
        try{
            return ResponseEntity.ok(service.obtenerDisciplinaCompleta(id));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener la disciplina"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/alta")
    public ResponseEntity altaDisciplina(@RequestBody DTODisciplinas disciplina) {
        try{
            service.altaDisciplina(disciplina);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de alta la disciplina"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity modificarDisciplina(@RequestBody DTODisciplinas disciplina) {
        try{
            service.modificarDisciplina(disciplina);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo actualizar la disciplina"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/baja")
    public ResponseEntity bajaDisciplina(@RequestParam(name="id", required=true) Long id) {
        try{
            service.bajaDisciplina(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de baja la disciplina"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}

