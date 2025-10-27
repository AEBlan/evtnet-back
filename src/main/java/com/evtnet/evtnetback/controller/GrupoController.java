package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.*;
import com.evtnet.evtnetback.Services.GrupoServiceImpl;
import com.evtnet.evtnetback.dto.grupos.*;
import com.evtnet.evtnetback.Services.GrupoService;
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
            @RequestParam Long idGrupo,
            @RequestParam String texto) throws Exception {
        return ResponseEntity.ok(service.buscarUsuariosParaAgregar(idGrupo, texto));}

    @PostMapping("/crearGrupo")
    public ResponseEntity<DTORespuestaCrearGrupo> crearGrupo(@RequestBody DTOCrearGrupo dto) throws Exception {
        return ResponseEntity.ok(service.crearGrupo(dto));
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
