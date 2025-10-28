package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.entity.ParametroSistema;
import com.evtnet.evtnetback.service.ParametroSistemaServiceImpl;
import com.evtnet.evtnetback.dto.parametroSistema.DTOParametroSistema;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/parametro")
@AllArgsConstructor
public class ParametroSistemaController extends BaseControllerImpl <ParametroSistema, ParametroSistemaServiceImpl> {
    private final ParametroSistemaServiceImpl service;
    @GetMapping("/obtenerListaParametros")
    public ResponseEntity obtenerListaParametrosSistema(@RequestParam(name = "page", defaultValue = "0") int page) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                    page, 10, org.springframework.data.domain.Sort.by("id").ascending()
            );
            return ResponseEntity.ok(service.obtenerListaParametroSistema(pageable));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los parámetros del sistema"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping ("/obtenerParametroCompleto")
    public ResponseEntity obtenerParametroCompleto(@RequestParam(name="id", required=true) Long id) {
        try{
            return ResponseEntity.ok(service.obtenerParametroSistemaCompleto(id));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el parámetro del sistema"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/alta")
    public ResponseEntity altaParametroSistema(@RequestBody DTOParametroSistema parametroSistema) {
        try{
            service.altaParametroSistema(parametroSistema);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de alta el parámetro del sistema"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity modificarParametroSistema(@RequestBody DTOParametroSistema parametroSistema) {
        try{
            service.modificarParametroSistema(parametroSistema);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo actualizar el parámetro del sistema"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/baja")
    public ResponseEntity bajaParametroSistema(@RequestParam(name="id", required=true) Long id) {
        try{
            service.bajaParametroSistema(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de baja el parámetro del sistema"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
