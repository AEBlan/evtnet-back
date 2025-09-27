package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.ModoEvento;
import com.evtnet.evtnetback.Services.ModoEventoServiceImpl;
import com.evtnet.evtnetback.dto.disciplinas.DTOBusquedaDisciplina;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinas;
import com.evtnet.evtnetback.dto.modoEvento.DTOModoEvento;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/modosDeEvento")
@AllArgsConstructor
public class ModoEventoController extends BaseControllerImpl<ModoEvento, ModoEventoServiceImpl> {

    private final ModoEventoServiceImpl service;

    // GET /modosDeEvento/buscar?text=...
    @GetMapping("/buscar")
    public ResponseEntity<List<DTOModoEvento>> buscar(@RequestParam String text) {
        try { return ResponseEntity.ok(service.buscarPorNombre(text)); 
        }catch (Exception e) { return ResponseEntity.badRequest().build(); }
    }

    @GetMapping ("/obtenerListaModosEvento")
    public ResponseEntity obtenerListaModosEvento(@RequestParam(name = "page", defaultValue = "0") int page) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                    page, 10, org.springframework.data.domain.Sort.by("id").ascending()
            );
            return ResponseEntity.ok(service.obtenerListaModosEvento(pageable));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los modos de evento"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping ("/obtenerModoEventoCompleto")
    public ResponseEntity obtenerModoEventoCompleto(@RequestParam(name="id", required=true) Long id) {
        try{
            return ResponseEntity.ok(service.obtenerModoEventoCompleto(id));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el modo de evento"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/alta")
    public ResponseEntity altaModoEvento(@RequestBody DTOModoEvento modoEvento) {
        try{
            service.altaModoEvento(modoEvento);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de alta el modo de evento"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity modificarModoEvento(@RequestBody DTOModoEvento modoEvento) {
        try{
            service.modificarModoEvento(modoEvento);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo actualizar el modo de evento"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/baja")
    public ResponseEntity bajaModoEvento(@RequestParam(name="id", required=true) Long id) {
        try{
            service.bajaModoEvento(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de baja el modo de evento"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
