package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.MedioDePago;
import com.evtnet.evtnetback.Services.MedioDePagoServiceImpl;
import com.evtnet.evtnetback.dto.medioDePago.DTOMedioDePago;
import com.evtnet.evtnetback.error.HttpErrorException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medioPago")
@AllArgsConstructor
public class MedioDePagoController extends BaseControllerImpl <MedioDePago, MedioDePagoServiceImpl> {
    private final MedioDePagoServiceImpl service;

    @GetMapping("/obtenerMediosPago")
    public ResponseEntity obtenerListaMediosPago(@RequestParam(name = "page", defaultValue = "0") int page) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(
                    page, 10, org.springframework.data.domain.Sort.by("id").ascending()
            );
            return ResponseEntity.ok(service.obtenerListaMedioDePago(pageable));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudieron obtener los medios de pago"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping ("/obtenerMedioPagoCompleto")
    public ResponseEntity obtenerMedioPagoCompleto(@RequestParam(name="id", required=true) Long id) {
        try{
            return ResponseEntity.ok(service.obtenerMedioDePagoCompleto(id));
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo obtener el medio de pago"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PostMapping("/alta")
    public ResponseEntity altaMedioPago(@RequestBody DTOMedioDePago medioPago) {
        try{
            service.altaMedioDePago(medioPago);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de alta el medio de pago"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/modificar")
    public ResponseEntity modificarMedioPago(@RequestBody DTOMedioDePago medioPago) {
        try{
            service.modificarMedioDePago(medioPago);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo actualizar el medio de pago"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/baja")
    public ResponseEntity bajaMedioPago(@RequestParam(name="id", required=true) Long id) {
        try{
            service.bajaMedioDePago(id);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            HttpErrorException error = new HttpErrorException(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se pudo dar de baja el medio de pago"
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
