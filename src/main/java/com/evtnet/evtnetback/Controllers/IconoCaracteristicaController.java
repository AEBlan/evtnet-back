package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.IconoCaracteristica;
import com.evtnet.evtnetback.Services.IconoCaracteristicaServiceImpl;
import com.evtnet.evtnetback.dto.iconoCaracteristica.DTOIconoCaracteristica;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/iconosCaracteristicas")
@AllArgsConstructor
public class IconoCaracteristicaController extends BaseControllerImpl <IconoCaracteristica, IconoCaracteristicaServiceImpl> {

    private final IconoCaracteristicaServiceImpl service;

    @GetMapping("/obtenerIconosCaracteristicas")
    public ResponseEntity obtenerListaiIconosCaracteristica(@RequestParam(name = "page", defaultValue = "0") int page) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                    page, 10, org.springframework.data.domain.Sort.by("id").ascending()
            );
            return ResponseEntity.ok(service.obtenerListaIconoCaracteristica(pageable));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los íconos de características"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping ("/obtenerIconoCaracteristicaCompleto")
    public ResponseEntity obtenerIconoCaracteristicaCompleto(@RequestParam(name="id", required=true) Long id) {
        try{
            return ResponseEntity.ok(service.obtenerIconoCaracteristicaCompleto(id));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el ícono de característica"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/alta")
    public ResponseEntity altaIconoCaracteristica(@RequestBody DTOIconoCaracteristica iconoCaracteristica) {
        try{
            service.altaIconoCaracteristica(iconoCaracteristica);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de alta el ícono de característica"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity modificarIconoCaracteristica(@RequestBody DTOIconoCaracteristica iconoCaracteristica) {
        try{
            service.modificarIconoCaracteristica(iconoCaracteristica);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo actualizar el ícono de característica"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/baja")
    public ResponseEntity bajaIconoCaracteristica(@RequestParam(name="id", required=true) Long id) {
        try{
            service.bajaIconoCaracteristica(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de baja el ícono de característica"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
