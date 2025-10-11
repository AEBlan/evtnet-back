// src/main/java/com/evtnet/evtnetback/Controllers/EventoController.java
package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.dto.comunes.CantidadResponse;
import com.evtnet.evtnetback.dto.comunes.IdResponse;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;
import com.evtnet.evtnetback.Services.EventoService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoController {

    private final EventoService service; /* 

    @PutMapping("/buscar")
    public ResponseEntity<List<DTOResultadoBusquedaEventos>> buscar(@RequestBody DTOBusquedaEventos filtro) {
        return ResponseEntity.ok(service.buscar(filtro));
    }

    @PutMapping("/buscarMisEventos")
    public ResponseEntity<List<DTOResultadoBusquedaMisEventos>> buscarMisEventos(
            @RequestBody DTOBusquedaMisEventos filtro,
            Authentication authentication   // ✅ se inyecta el usuario logueado
    ) {
        String username = authentication.getName(); // ✅ nombre del usuario del token JWT
        return ResponseEntity.ok(service.buscarMisEventos(filtro, username));
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

    // --- NUEVOS para el front actual ---

    @GetMapping("/obtenerEventoParaInscripcion")
    public ResponseEntity<DTOEventoParaInscripcion> obtenerEventoParaInscripcion(@RequestParam long id) {
        return ResponseEntity.ok(service.obtenerEventoParaInscripcion(id));
    }

    @PutMapping("/verificarDatosPrePago")
    public ResponseEntity<Map<String, Boolean>> verificarDatosPrePago(@RequestBody DTOInscripcion dto) {
        return ResponseEntity.ok(Map.of("valido", service.verificarDatosPrePago(dto)));
    }

    @PostMapping("/inscribirse")
    public ResponseEntity<Void> inscribirse(@RequestBody DTOInscripcion dto) {
        service.inscribirse(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/desinscribirse")
    public ResponseEntity<Void> desinscribirse(@RequestParam long idEvento) {
        service.desinscribirse(idEvento);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/obtenerMontoDevolucionCancelacionInscripcion")
    public ResponseEntity<Map<String, Number>> obtenerMontoDevolucionCancelacionInscripcion(
            @RequestParam long idEvento, @RequestParam String username) {
        return ResponseEntity.ok(Map.of("monto", service.obtenerMontoDevolucionCancelacion(idEvento, username)));
    }

    @GetMapping("/obtenerDatosModificacionEvento")
    public ResponseEntity<DTOModificarEvento> obtenerDatosModificacionEvento(@RequestParam long id) {
        return ResponseEntity.ok(service.obtenerDatosModificacionEvento(id));
    }

    @PostMapping("/modificarEvento")
    public ResponseEntity<Void> modificarEvento(@RequestBody DTOModificarEvento dto) {
        service.modificarEvento(dto);
        return ResponseEntity.ok().build();
    }*/
}
