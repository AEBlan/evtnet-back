package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.service.ComisionPorInscripcionService;
import com.evtnet.evtnetback.dto.comisionPorInscripcion.DTOComisionPorInscripcion;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comisionPorInscripcion")
@AllArgsConstructor
public class ComisionPorInscripcionController  {

    private final ComisionPorInscripcionService service;
    @GetMapping("/obtenerListaComisiones")
    public ResponseEntity obtenerListaComisionPorOrganizacion(@RequestParam(name = "page", defaultValue = "0") int page,
                                                              @RequestParam(name = "activas", defaultValue = "true") boolean activas,
                                                              @RequestParam(name = "noActivas", defaultValue = "true") boolean noActivas) {
        try {
            return ResponseEntity.ok(service.obtenerListaComisionPorInscripcion(page, activas, noActivas));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener las comisiones por inscripción"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping ("/obtenerComisionCompleta")
    public ResponseEntity obtenerComisionPorInscripcionCompleto(@RequestParam(name="id", required=true) Long id) {
        try{
            return ResponseEntity.ok(service.obtenerComisionPorInscripcionCompleto(id));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener la comisión por inscripción"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/alta")
    public ResponseEntity altaComisionPorInscripcion(@RequestBody DTOComisionPorInscripcion comisionPorInscripcion) {
        try{
            service.altaComisionPorInscripcion(comisionPorInscripcion);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de alta la comisión por inscripcion"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity modificarComisionPorInscripcion(@RequestBody DTOComisionPorInscripcion comisionPorInscripcion) {
        try{
            service.modificarComisionPorInscripcion(comisionPorInscripcion);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo actualizar la comisión por inscripción"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/baja")
    public ResponseEntity bajaComisionPorInscripcion(@RequestParam(name="id", required=true) Long id) {
        try{
            service.bajaComisionPorInscripcion(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de baja la comisión por inscripción"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/restaurar")
    public ResponseEntity restaurarComisionPorInscripcion(@RequestParam(name="id", required=true) Long id) {
        try{
            service.restaurarComisionPorInscripcion(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo restaurar la comisión por inscripción"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
