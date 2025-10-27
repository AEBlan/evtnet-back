package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.EstadoSEP;
import com.evtnet.evtnetback.Services.EstadoSEPServiceImpl;
import com.evtnet.evtnetback.dto.estadoSEP.DTOEstadoSEP;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estadoSolicitudEspacioPublico")
@AllArgsConstructor
public class EstadoSEPController extends BaseControllerImpl <EstadoSEP, EstadoSEPServiceImpl> {
    private final EstadoSEPServiceImpl service;
    @GetMapping("/obtenerListaEstadoSolicitud")
    public ResponseEntity obtenerListaEstadoSEP(@RequestParam(name = "page", defaultValue = "0") int page) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                    page, 10, org.springframework.data.domain.Sort.by("id").ascending()
            );
            return ResponseEntity.ok(service.obtenerListaEstadoSEP(pageable));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los estados de denuncia de evento"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping ("/obtenerEstadoSolicitudCompleto")
    public ResponseEntity obtenerEstadoSEPCompleto(@RequestParam(name="id", required=true) Long id) {
        try{
            return ResponseEntity.ok(service.obtenerEstadoSEPCompleto(id));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el estado de denuncia de evento"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/alta")
    public ResponseEntity altaEstadoSEP(@RequestBody DTOEstadoSEP estadoSEP) {
        try{
            service.altaEstadoSEP(estadoSEP);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de alta el estado de denuncia de evento"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity modificarEstadoSEP(@RequestBody DTOEstadoSEP estadoSEP) {
        try{
            service.modificarEstadoSEP(estadoSEP);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo actualizar el estado de denuncia de evento"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/baja")
    public ResponseEntity bajaEstadoSEP(@RequestParam(name="id", required=true) Long id) {
        try{
            service.bajaEstadoSEP(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de baja el estado de denuncia de evento"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
