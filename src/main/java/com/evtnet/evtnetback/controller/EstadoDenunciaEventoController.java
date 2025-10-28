package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.entity.EstadoDenunciaEvento;
import com.evtnet.evtnetback.service.EstadoDenunciaEventoServiceImpl;
import com.evtnet.evtnetback.dto.estadoDenunciaEvento.DTOEstadoDenunciaEvento;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/estadoDenunciaEvento")
@AllArgsConstructor
public class EstadoDenunciaEventoController extends BaseControllerImpl <EstadoDenunciaEvento, EstadoDenunciaEventoServiceImpl> {

    private final EstadoDenunciaEventoServiceImpl service;

    @GetMapping("/obtenerListaEstadoDenuncia")
    public ResponseEntity obtenerListaEstadoDenuncia(@RequestParam(name = "page", defaultValue = "0") int page) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                    page, 10, org.springframework.data.domain.Sort.by("id").ascending()
            );
            return ResponseEntity.ok(service.obtenerListaEstadoDenunciaEvento(pageable));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los estados de denuncia de evento"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping ("/obtenerEstadoDenunciaEventoCompleto")
    public ResponseEntity obtenerEstadoDenunciaEventoCompleto(@RequestParam(name="id", required=true) Long id) {
        try{
            return ResponseEntity.ok(service.obtenerEstadoDenunciaEventoCompleto(id));
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
    public ResponseEntity altaEstadoDenunciaEvento(@RequestBody DTOEstadoDenunciaEvento estadoDenunciaEvento) {
        try{
            service.altaEstadoDenunciaEvento(estadoDenunciaEvento);
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
    public ResponseEntity modificarEstadoDenunciaEvento(@RequestBody DTOEstadoDenunciaEvento estadoDenunciaEvento) {
        try{
            service.modificarEstadoDenunciaEvento(estadoDenunciaEvento);
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
    public ResponseEntity bajaEstadoDenunciaEvento(@RequestParam(name="id", required=true) Long id) {
        try{
            service.bajaEstadoDenunciaEvento(id);
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
