package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.entity.TipoCalificacion;
import com.evtnet.evtnetback.service.TipoCalificacionServiceImpl;
import com.evtnet.evtnetback.dto.tipoCalificacion.DTOTipoCalificacion;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tipoCalificacion")
@AllArgsConstructor
public class TipoCalificacionController extends BaseControllerImpl <TipoCalificacion, TipoCalificacionServiceImpl> {
    private final TipoCalificacionServiceImpl service;

    @GetMapping("/obtenerTiposCalificacion")
    public ResponseEntity obtenerListaTiposCalificacion(@RequestParam(name = "page", defaultValue = "0") int page,
                                                        @RequestParam(name = "vigentes", defaultValue = "0") boolean vigentes,
                                                        @RequestParam(name = "dadasDeBaja", defaultValue = "0") boolean dadasDeBaja
    ) {
        try {
            return ResponseEntity.ok(service.obtenerListaTipoCalificacion(page, vigentes, dadasDeBaja));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los tipos de calificación"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerTiposCalificacionSelect")
    public ResponseEntity obtenerTiposCalificacionSelect() {
        try {
            return ResponseEntity.ok(service.obtenerTiposCalificacionSelect());
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los tipos de calificación"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping ("/obtenerTipoCalificacionCompleto")
    public ResponseEntity obtenerTipoCalificacionCompleto(@RequestParam(name="id", required=true) Long id) {
        try{
            return ResponseEntity.ok(service.obtenerTipoCalificacionCompleto(id));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el tipo de calificación"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/alta")
    public ResponseEntity altaTipoCalificacion(@RequestBody DTOTipoCalificacion tipoCalificacion) {
        try{
            service.altaTipoCalificacion(tipoCalificacion);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de alta el tipo de calificación. "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity modificarTipoCalificacion(@RequestBody DTOTipoCalificacion tipoCalificacion) {
        try{
            service.modificarTipoCalificacion(tipoCalificacion);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo actualizar el tipo de calificación. "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/baja")
    public ResponseEntity bajaTipoCalificacion(@RequestParam(name="id", required=true) Long id) {
        try{
            service.bajaTipoCalificacion(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de baja el tipo de calificación"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/restaurar")
    public ResponseEntity restaurarTipoCalificacion(@RequestParam(name="id", required=true) Long id) {
        try{
            service.restaurarTipoCalificacion(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo restaurar el tipo de calificación. "+e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
