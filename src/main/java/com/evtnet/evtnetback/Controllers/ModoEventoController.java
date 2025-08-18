package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.ModoEvento;
import com.evtnet.evtnetback.Services.ModoEventoServiceImpl;
import com.evtnet.evtnetback.dto.modoEvento.DTOModoEvento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/modosDeEvento")
public class ModoEventoController extends BaseControllerImpl<ModoEvento, ModoEventoServiceImpl> {

    private final ModoEventoServiceImpl service;

    public ModoEventoController(ModoEventoServiceImpl service) {
        this.service = service;
    }

    // GET /modosDeEvento/buscar?text=...
    @GetMapping("/buscar")
    public ResponseEntity<List<DTOModoEvento>> buscar(@RequestParam String text) {
        try {
            final String q = text == null ? "" : text.trim().toLowerCase();

            List<DTOModoEvento> result = service.findAll().stream()
                    // si usás “baja lógica”, podés filtrar las dadas de baja:
                    // .filter(me -> me.getFechaHoraBaja() == null)
                    .filter(me -> Objects.nonNull(me.getNombre()))
                    .filter(me -> q.isEmpty() || me.getNombre().toLowerCase().contains(q))
                    .map(this::toDto)
                    .toList();

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private DTOModoEvento toDto(ModoEvento me) {
        return new DTOModoEvento(me.getId(), me.getNombre());
    }
}
