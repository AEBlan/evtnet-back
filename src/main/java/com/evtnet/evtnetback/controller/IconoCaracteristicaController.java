package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.entity.IconoCaracteristica;
import com.evtnet.evtnetback.service.IconoCaracteristicaService;
import com.evtnet.evtnetback.service.IconoCaracteristicaServiceImpl;
import com.evtnet.evtnetback.dto.iconoCaracteristica.DTOIconoCaracteristica;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/iconosCaracteristicas")
@AllArgsConstructor
public class IconoCaracteristicaController extends BaseControllerImpl <IconoCaracteristica, IconoCaracteristicaServiceImpl> {

    private final IconoCaracteristicaService iconoCaracteristicaService;

    @GetMapping("/obtenerIconosCaracteristicas")
    public ResponseEntity obtenerListaiIconosCaracteristica(@RequestParam(name = "page", defaultValue = "0") int page) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                    page, 10, org.springframework.data.domain.Sort.by("id").ascending()
            );
            return ResponseEntity.ok(iconoCaracteristicaService.obtenerListaIconoCaracteristica(pageable));
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
            return ResponseEntity.ok(iconoCaracteristicaService.obtenerIconoCaracteristicaCompleto(id));
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
            iconoCaracteristicaService.altaIconoCaracteristica(iconoCaracteristica);
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
            iconoCaracteristicaService.modificarIconoCaracteristica(iconoCaracteristica);
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
            iconoCaracteristicaService.bajaIconoCaracteristica(id);
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

    @GetMapping("/obtener")
    public ResponseEntity obtenerIconosEspacio(@RequestParam(name = "idIcono", required = true) Long idIcono) {
        try {
            IconoCaracteristica icono=iconoCaracteristicaService.obtenerIconosEspacio(idIcono);
            Path path = Paths.get(icono.getImagen());
            byte[] imagenBytes = Files.readAllBytes(path);

            // Deducir tipo MIME (por ejemplo, image/png o image/jpeg)
            String mimeType = Files.probeContentType(path);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType))
                    .body(imagenBytes);
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el ícono"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerCaracteristicasSubEspacio")
    public ResponseEntity obtenerCaracteristicasEspacio(@RequestParam(name = "idSubEspacio", required = true) Long idSubEspacio) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(iconoCaracteristicaService.obtenerCaracteristicasSubEspacio(idSubEspacio));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el ícono"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/obtenerListaIconos")
    public ResponseEntity obtenerListaIconos() {
        try {
            return ResponseEntity.ok(iconoCaracteristicaService.obtenerListaIcono());
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los íconos de características"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

}
