package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.dto.mascota.DTOInstanciaMascota;
import com.evtnet.evtnetback.dto.mascota.DTOAltaInstanciaMascota;
import com.evtnet.evtnetback.dto.mascota.DTOModificarInstanciaMascota;
import com.evtnet.evtnetback.entity.InstanciaMascota;
import com.evtnet.evtnetback.service.InstanciaMascotaService;
import com.evtnet.evtnetback.service.InstanciaMascotaServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/instanciasMascota")
@AllArgsConstructor
public class InstanciaMascotaController extends BaseControllerImpl<InstanciaMascota, InstanciaMascotaServiceImpl> {

    private final InstanciaMascotaService instanciaMascotaService;

    @GetMapping("/obtenerInstanciasMascota")
    public ResponseEntity<Page<DTOInstanciaMascota>> obtenerListaInstanciasMascota(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "texto", required = false) String texto) throws Exception {
        return ResponseEntity.ok(instanciaMascotaService.obtenerListaInstanciaMascota(page, texto));
    }

    @GetMapping("/obtenerInstanciaMascotaCompleta")
    public ResponseEntity<DTOInstanciaMascota> obtenerInstanciaMascotaCompleta(@RequestParam(name = "id", required = true) Long id) throws Exception {
        return ResponseEntity.ok(instanciaMascotaService.obtenerInstanciaMascotaCompleta(id));
    }

    @PostMapping("/alta")
    public ResponseEntity<Void> altaInstanciaMascota(@RequestBody DTOAltaInstanciaMascota instanciaMascota) throws Exception {
        instanciaMascotaService.altaInstanciaMascota(instanciaMascota);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/modificar")
    public ResponseEntity<Void> modificarInstanciaMascota(@RequestBody DTOModificarInstanciaMascota instanciaMascota) throws Exception {
        instanciaMascotaService.modificarInstanciaMascota(instanciaMascota);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/baja")
    public ResponseEntity<Void> bajaInstanciaMascota(@RequestParam(name = "id", required = true) Long id) throws Exception {
        instanciaMascotaService.bajaInstanciaMascota(id);
        return ResponseEntity.ok().build();
    }
}