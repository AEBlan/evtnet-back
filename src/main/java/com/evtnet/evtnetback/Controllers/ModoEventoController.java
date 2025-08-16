package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.ModoEvento;
import com.evtnet.evtnetback.Services.ModoEventoServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/modoevento") // guion medio para separar palabras
public class ModoEventoController extends BaseControllerImpl<ModoEvento, ModoEventoServiceImpl> {

    private final ModoEventoServiceImpl service;

    public ModoEventoController(ModoEventoServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getByIdSoloIdYNombre(@PathVariable Long id) {
        try {
            ModoEvento me = service.findById(id);
            Map<String, Object> body = new HashMap<>();
            body.put("id", me.getId());
            body.put("nombre", me.getNombre());
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

