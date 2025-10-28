package com.evtnet.evtnetback.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evtnet.evtnetback.entity.SuperEvento;
import com.evtnet.evtnetback.service.SuperEventoService;
import com.evtnet.evtnetback.service.SuperEventoServiceImpl;
import com.evtnet.evtnetback.dto.supereventos.*;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;
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

    @GetMapping("/obtenerSuperEventoEditar")
    public ResponseEntity<DTOSuperEventoEditar> obtenerSuperEventoEditar(@RequestParam Long id) throws Exception {
        return ResponseEntity.ok()
            .body(service.obtenerSuperEventoEditar(id));
    }

    @PutMapping("/editarSuperEvento")
    public ResponseEntity<Void> editarSuperEvento(@RequestBody DTOSuperEventoEditar data) throws Exception {
        service.editarSuperEvento(data);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/dejarDeAdministrar")
    public ResponseEntity<Void> dejarDeAdministrar(@RequestParam Long supereventoId) throws Exception {
        service.dejarDeAdministrar(supereventoId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/baja")
    public ResponseEntity<Void> baja(@RequestParam Long supereventoId) throws Exception {
        service.baja(supereventoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("buscarEventosNoVinculados")
    public ResponseEntity<List<DTOBusquedaEvento>> buscarEventosNoVinculados(@RequestParam Long idSuperevento, @RequestParam String texto) throws Exception {
        return ResponseEntity.ok()
            .body(service.buscarEventosNoVinculados(idSuperevento, texto));
    }

    @GetMapping("/obtenerAdministradores")
    public ResponseEntity<DTOAdministradoresSuperevento> obtenerAdministradores(@RequestParam Long idEvento) throws Exception {
        return ResponseEntity.ok()
            .body(service.obtenerAdministradores(idEvento));
    }

    @PostMapping("/agregarAdministrador")
    public ResponseEntity<Void> agregarAdministrador(@RequestParam Long idEvento, @RequestParam String username) throws Exception {
        service.agregarAdministrador(idEvento, username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/quitarAdministrador")
    public ResponseEntity<Void> quitarAdministrador(@RequestParam Long idEvento, @RequestParam String username) throws Exception {
        service.quitarAdministrador(idEvento, username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/entregarOrganizador")
    public ResponseEntity<Void> entregarOrganizador(@RequestParam Long idEvento, @RequestParam String username) throws Exception {
        service.entregarOrganizador(idEvento, username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/buscarUsuariosNoAdministradores")
    public ResponseEntity<List<DTOBusquedaUsuario>> buscarUsuariosNoAdministradores(@RequestParam Long idEvento, @RequestParam String texto) throws Exception {
        return ResponseEntity.ok()
            .body(service.buscarUsuariosNoAdministradores(idEvento, texto));
    }

}
