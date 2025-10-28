package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.entity.MotivoCalificacion;
import com.evtnet.evtnetback.service.MotivoCalificacionServiceImpl;
import com.evtnet.evtnetback.dto.motivoCalificacion.DTOMotivoCalificacion;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/motivoCalificacion")
@AllArgsConstructor
public class MotivoCalificacionController extends BaseControllerImpl <MotivoCalificacion, MotivoCalificacionServiceImpl> {
    private final MotivoCalificacionServiceImpl service;

    @GetMapping("/obtenerMotivosCalificacion")
    public ResponseEntity obtenerListaMotivosCalificacion(@RequestParam(name = "page", defaultValue = "0") int page) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                    page, 10, org.springframework.data.domain.Sort.by("id").ascending()
            );
            return ResponseEntity.ok(service.obtenerListaMotivoCalificacion(pageable));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los íconos de características"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping ("/obtenerMotivoCalificacionCompleto")
    public ResponseEntity obtenerMotivoCalificacionCompleto(@RequestParam(name="id", required=true) Long id) {
        try{
            return ResponseEntity.ok(service.obtenerMotivoCalificacionCompleto(id));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el motivo de calificación"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/alta")
    public ResponseEntity altaMotivoCalificacion(@RequestBody DTOMotivoCalificacion motivoCalificacion) {
        try{
            service.altaMotivoCalificacion(motivoCalificacion);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de alta el motivo de calificación"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity modificarMotivoCalificacion(@RequestBody DTOMotivoCalificacion motivoCalificacion) {
        try{
            service.modificarMotivoCalificacion(motivoCalificacion);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo actualizar el motivo de calificación"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/baja")
    public ResponseEntity bajaMotivoCalificacion(@RequestParam(name="id", required=true) Long id) {
        try{
            service.bajaMotivoCalificacion(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de baja el motivo de calificación"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
