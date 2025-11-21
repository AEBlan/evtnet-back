package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.service.GrupoServiceImpl;
import com.evtnet.evtnetback.dto.grupos.*;
import com.evtnet.evtnetback.service.GrupoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/grupos")
@RequiredArgsConstructor
public class GrupoController extends BaseControllerImpl <Grupo, GrupoServiceImpl> {
    private final GrupoService service;

    @GetMapping("/obtenerGrupos")
    public Page<DTOGrupoSimple> obtenerGrupos(
            @RequestParam String texto,
            @RequestParam int page) {
        return service.obtenerGrupos(texto, page);
    }

    @GetMapping("/adminObtenerGrupo")
    public DTOAdminGrupo adminObtenerGrupo(@RequestParam Long id) {
        return service.adminObtenerGrupo(id);
    }

    @GetMapping("/obtenerMisGrupos")
    public List<DTOGrupoMisGrupos> obtenerMisGrupos() {
        return service.obtenerMisGrupos();
    }

    // GET grupos/obtenerGrupo?id=123
    @GetMapping("/obtenerGrupo")
    public ResponseEntity<DTOGrupo> obtenerGrupo(@RequestParam("id") Long id) throws Exception {
        return ResponseEntity.ok(service.obtenerGrupo(id));
    }

    // DELETE grupos/salir?id=123
    @DeleteMapping("/salir")
    public ResponseEntity<Void> salir(@RequestParam("id") Long id) throws Exception {
        service.salir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscarUsuariosParaAgregar")
    public ResponseEntity<List<DTOBusquedaUsuario>> buscarUsuariosParaAgregar(
            @RequestParam(required = false) Long idGrupo,
            @RequestParam String texto) throws Exception {
        return ResponseEntity.ok(service.buscarUsuariosParaAgregar(idGrupo, texto));}

    @PostMapping("/crearGrupo")
    public ResponseEntity<DTORespuestaCrearGrupo> crearGrupo(@RequestBody DTOCrearGrupo dto) throws Exception {
        return ResponseEntity.ok(service.crearGrupo(dto));
    }
    @PostMapping("/toggleInvitacion")
    public void toggleInvitacion(@RequestParam Long idGrupo, @RequestParam Boolean aceptar) throws Exception {
        service.toggleInvitacion(idGrupo, aceptar);
    }
    @GetMapping("/obtenerTiposUsuarioGrupo")
    public ResponseEntity<List<DTOTipoUsuarioGrupo>> obtenerTiposUsuarioGrupo() {
        return ResponseEntity.ok(service.obtenerTiposUsuarioGrupo());
    }

    @GetMapping("/obtenerDatosModificarGrupo")
    public ResponseEntity<DTOModificarGrupo> obtenerDatosModificarGrupo(@RequestParam Long idGrupo) throws Exception {
        return ResponseEntity.ok(service.obtenerDatosModificarGrupo(idGrupo));
    }

    @PutMapping("/modificarGrupo")
    public ResponseEntity<Void> modificarGrupo(@RequestBody DTOModificarGrupo dto) throws Exception {
        service.modificarGrupo(dto);
        return ResponseEntity.ok().build();
    }
}
