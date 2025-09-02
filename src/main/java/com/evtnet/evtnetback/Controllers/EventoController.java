package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.dto.comunes.CantidadResponse;
import com.evtnet.evtnetback.dto.comunes.IdResponse;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.Services.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService service;

    @PutMapping("/buscar")
    public ResponseEntity<List<DTOResultadoBusquedaEventos>> buscar(@RequestBody DTOBusquedaEventos filtro) {
        return ResponseEntity.ok(service.buscar(filtro));
    }

    @PutMapping("/buscarMisEventos")
    public ResponseEntity<List<DTOResultadoBusquedaMisEventos>> buscarMisEventos(@RequestBody DTOBusquedaMisEventos filtro) {
        return ResponseEntity.ok(service.buscarMisEventos(filtro));
    }

    @GetMapping("/obtenerEvento")
    public ResponseEntity<DTOEvento> obtenerEvento(@RequestParam long id) {
        return ResponseEntity.ok(service.obtenerEventoDetalle(id));
    }

    @GetMapping("/obtenerDatosCreacionEvento")
    public ResponseEntity<DTODatosCreacionEvento> obtenerDatosCreacionEvento(@RequestParam String idEspacio) {
        Long espacioId = "null".equalsIgnoreCase(idEspacio) ? null : Long.parseLong(idEspacio);
        return ResponseEntity.ok(service.obtenerDatosCreacionEvento(espacioId));
    }

    // 🔹 Ahora recibe DTOEventoCreate
    @PostMapping("/crearEvento")
    public ResponseEntity<IdResponse> crearEvento(@RequestBody DTOEventoCreate req) {
        long id = service.crearEvento(req);
        return ResponseEntity.ok(new IdResponse(id));
    }

    @GetMapping("/obtenerCantidadEventosSuperpuestos")
    public ResponseEntity<CantidadResponse> obtenerCantidadEventosSuperpuestos(
            @RequestParam long idEspacio,
            @RequestParam long fechaHoraDesde,
            @RequestParam long fechaHoraHasta) {
        int cantidad = service.obtenerCantidadEventosSuperpuestos(idEspacio, fechaHoraDesde, fechaHoraHasta);
        return ResponseEntity.ok(new CantidadResponse(cantidad));
    }
}
