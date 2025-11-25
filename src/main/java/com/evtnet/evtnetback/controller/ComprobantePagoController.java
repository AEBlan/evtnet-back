package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.dto.comprobante.DTOComprobante;
import com.evtnet.evtnetback.dto.comprobante.DTOComprobanteSimple;
import com.evtnet.evtnetback.service.ComprobantePagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comprobantes")
@RequiredArgsConstructor
public class ComprobantePagoController {

    private final ComprobantePagoService comprobantePagoService;

    @GetMapping("/obtener")
    public ResponseEntity<DTOComprobante> obtener(@RequestParam(name = "numero") Long numero) throws Exception {
        return ResponseEntity.ok(comprobantePagoService.obtener(numero));
    }

    @GetMapping("/obtenerArchivo")
    public ResponseEntity<byte[]> obtenerArchivo(@RequestParam(name = "numero") Long numero) throws Exception {
        byte[] file = comprobantePagoService.obtenerArchivo(numero);
        String contentType = "application/pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }

    @GetMapping("/obtenerMisComprobantes")
    public ResponseEntity<List<DTOComprobanteSimple>> obtenerMisComprobantes() throws Exception {
        return ResponseEntity.ok(comprobantePagoService.obtenerMisComprobantes());
    }
}
