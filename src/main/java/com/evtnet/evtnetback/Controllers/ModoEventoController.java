package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.ModoEvento;
import com.evtnet.evtnetback.Services.ModoEventoServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;            

@RestController
@RequestMapping("/modosDeEvento")
public class ModoEventoController extends BaseControllerImpl<ModoEvento, ModoEventoServiceImpl> {

    private final ModoEventoServiceImpl service;

    public ModoEventoController(ModoEventoServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(@RequestParam String text) {
        try {
            List<ModoEvento> lista = service.findAll();
            List<Map<String, Object>> result = lista.stream()
                    .filter(me -> me.getNombre() != null &&
                                  me.getNombre().toLowerCase().contains(text.toLowerCase()))
                    .map(me -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("id", me.getId());
                        m.put("nombre", me.getNombre());
                        return m;
                    })
                    .toList();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
