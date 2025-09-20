package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.Espacio;
import com.evtnet.evtnetback.Services.EspacioService;
import com.evtnet.evtnetback.dto.espacios.DTOCrearEspacio;
import com.evtnet.evtnetback.dto.espacios.DTOEspacioDetalle;
import com.evtnet.evtnetback.dto.comunes.IdResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // GetMapping, PathVariable, etc.

import java.net.URI;

@RestController
@RequestMapping("/api/espacios")
@CrossOrigin
public class EspacioController {

    private final EspacioService espacioService;

    public EspacioController(EspacioService espacioService) {
        this.espacioService = espacioService;
    }

    @PostMapping
    public ResponseEntity<IdResponse> crear(@Valid @RequestBody DTOCrearEspacio dto) {
        Long usuarioActualId = currentUserId();
        IdResponse id = espacioService.crearEspacioPrivado(dto, usuarioActualId);
        return ResponseEntity.created(URI.create("/api/espacios/" + id.id())).body(id);
    }

    @GetMapping("/{id}")
    public DTOEspacioDetalle detalle(@PathVariable Long id) {
        return espacioService.detalle(id);
    }

    private Long currentUserId() {
        try {
            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() != null) {
                Object p = auth.getPrincipal();
                try { return (Long) p.getClass().getMethod("getId").invoke(p); } catch (Exception ignored) {}
                if (p instanceof org.springframework.security.core.userdetails.User u) return Long.valueOf(u.getUsername());
            }
        } catch (Exception ignored) {}
        return 1L;
    }
}

