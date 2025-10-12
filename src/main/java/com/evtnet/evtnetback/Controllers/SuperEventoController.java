package com.evtnet.evtnetback.Controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evtnet.evtnetback.Entities.SuperEvento;
import com.evtnet.evtnetback.Services.SuperEventoService;
import com.evtnet.evtnetback.Services.SuperEventoServiceImpl;
import com.evtnet.evtnetback.dto.supereventos.*;

import com.evtnet.evtnetback.dto.comunes.IdResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/supereventos")
@RequiredArgsConstructor
public class SuperEventoController extends BaseControllerImpl <SuperEvento, SuperEventoServiceImpl> {
    
    private final SuperEventoService service;
    
    @GetMapping("/buscarAdministrados")
    public ResponseEntity<List<DTOBusquedaAdministrados>> buscarAdministrados(@RequestParam String text) throws Exception {
        return ResponseEntity.ok()
            .body(service.buscarAdministrados(text));
    }

    @PutMapping("/buscarMisSuperEventos")
    public ResponseEntity<List<DTOResultadoBusquedaMisSuperEventos>> buscarMisSuperEventos(@RequestBody DTOBusquedaMisSuperEventos data) throws Exception {
        return ResponseEntity.ok()
            .body(service.buscarMisSuperEventos(data));
    }

    @GetMapping("/obtenerSuperEvento")
    public ResponseEntity<DTOSuperEvento> obtenerSuperEvento(@RequestParam Long id) throws Exception {
        return ResponseEntity.ok()
            .body(service.obtenerSuperEvento(id));
    }

    @PostMapping("/crearSuperEvento")
    public ResponseEntity<IdResponse> crearSuperEvento(@RequestBody DTOCrearSuperEvento data) throws Exception {
        long id = service.crearSuperEvento(data);
        return ResponseEntity.ok(new IdResponse(id));
    }
}
