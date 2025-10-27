package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Services.ComisionPorOrganizacionService;
import com.evtnet.evtnetback.dto.comisionPorOrganizacion.DTOComisionPorOrganizacion;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comisionPorOrganizacion")
@AllArgsConstructor
public class ComisionPorOrganizacionController  {
    
    private final ComisionPorOrganizacionService service;
    @GetMapping("/obtenerListaComisiones")
    public ResponseEntity obtenerListaComisionPorOrganizacion(@RequestParam(name = "page", defaultValue = "0") int page) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                    page, 10, org.springframework.data.domain.Sort.by("id").ascending()
            );
            return ResponseEntity.ok(service.obtenerListaComisionPorOrganizacion(pageable));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener las comisiones por organización"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping ("/obtenerComisionCompleta")
    public ResponseEntity obtenerComisionPorOrganizacionCompleto(@RequestParam(name="id", required=true) Long id) {
        try{
            return ResponseEntity.ok(service.obtenerComisionPorOrganizacionCompleto(id));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener la comisión por organización"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/alta")
    public ResponseEntity altaComisionPorOrganizacion(@RequestBody DTOComisionPorOrganizacion comisionPorOrganizacion) {
        try{
            service.altaComisionPorOrganizacion(comisionPorOrganizacion);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de alta la comisión por organización"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity modificarComisionPorOrganizacion(@RequestBody DTOComisionPorOrganizacion comisionPorOrganizacion) {
        try{
            service.modificarComisionPorOrganizacion(comisionPorOrganizacion);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo actualizar la comisión por organización"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/baja")
    public ResponseEntity bajaComisionPorOrganizacion(@RequestParam(name="id", required=true) Long id) {
        try{
            service.bajaComisionPorOrganizacion(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de baja la comisión por organización"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
