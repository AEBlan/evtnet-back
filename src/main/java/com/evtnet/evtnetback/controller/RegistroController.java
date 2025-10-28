package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.entity.Registro;
import com.evtnet.evtnetback.service.RegistroService;
import com.evtnet.evtnetback.service.RegistroServiceImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.evtnet.evtnetback.dto.registros.DTOFiltrosRegistro;
import com.evtnet.evtnetback.dto.registros.DTORegistro;
import com.evtnet.evtnetback.dto.registros.DTORegistroMeta;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;


@RestController
@RequestMapping("/registros")
@RequiredArgsConstructor
public class RegistroController extends BaseControllerImpl<Registro, RegistroServiceImpl> {

    private final RegistroService service;
    
    @GetMapping("/obtenerRegistros")
    public ResponseEntity<List<DTORegistroMeta>> obtenerRegistros() throws Exception {
        return ResponseEntity.ok(service.obtenerRegistros());
    }
    
    @GetMapping("/obtenerRegistroFormateado")
    public ResponseEntity<DTORegistroMeta> obtenerRegistroFormateado(@RequestParam String nombre) throws Exception {
        return ResponseEntity.ok(service.obtenerRegistroFormateado(nombre));
    }
    
    @PutMapping("/buscar")
    public ResponseEntity<List<DTORegistro>> buscar(@RequestParam String registro, @RequestBody DTOFiltrosRegistro filtros) throws Exception {
        return ResponseEntity.ok(service.buscar(registro, filtros));
    }
    
    @GetMapping("/obtenerEntidades")
    public ResponseEntity<List<String>> obtenerEntidades(@RequestParam String registro) throws Exception {
        return ResponseEntity.ok(service.obtenerEntidades(registro));
    }
    
    @GetMapping("/obtenerAcciones")
    public ResponseEntity<List<String>> obtenerAcciones(@RequestParam String registro) throws Exception {
        return ResponseEntity.ok(service.obtenerAcciones(registro));
    }
    
    @GetMapping("/buscarUsuarios")
    public ResponseEntity<List<DTOBusquedaUsuario>> buscarUsuarios(@RequestParam String texto) throws Exception {
        return ResponseEntity.ok(service.buscarUsuarios(texto));
    }
}
