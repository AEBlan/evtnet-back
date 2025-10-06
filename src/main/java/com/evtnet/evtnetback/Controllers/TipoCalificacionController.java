package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.TipoCalificacion;
import com.evtnet.evtnetback.Services.TipoCalificacionServiceImpl;
import com.evtnet.evtnetback.Services.TipoCalificacionServiceImpl;
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
    public ResponseEntity obtenerListaTiposCalificacion(@RequestParam(name = "page", defaultValue = "0") int page) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                    page, 10, org.springframework.data.domain.Sort.by("id").ascending()
            );
            return ResponseEntity.ok(service.obtenerListaTipoCalificacion(pageable));
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
                    "No se pudo dar de alta el tipo de calificación"
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
                    "No se pudo actualizar el tipo de calificación"
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
}
