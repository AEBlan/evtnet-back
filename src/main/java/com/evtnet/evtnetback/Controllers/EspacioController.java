package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.Espacio;
import com.evtnet.evtnetback.Services.EspacioServiceImpl;
// imports agregados para #US_ESP_1
import com.evtnet.evtnetback.dto.espacios.DTOCrearEspacio;
import com.evtnet.evtnetback.dto.espacios.DTOEspacioDetalle;
import com.evtnet.evtnetback.dto.comunes.IdResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import java.net.URI;


// #US_ESP_1: Registrar espacio privado
@PostMapping
public ResponseEntity<IdResponse> crear(@Valid @RequestBody DTOCrearEspacio dto) {
    Long usuarioActualId = currentUserId();            // lee del contexto (ver helper abajo)
    IdResponse id = espacioService.crearEspacioPrivado(dto, usuarioActualId);
    return ResponseEntity.created(URI.create("/api/espacios/" + id.getId())).body(id);
}

// detalle (flujo post-crear)
@GetMapping("/{id}")
public DTOEspacioDetalle detalle(@PathVariable Long id) {
    return espacioService.detalle(id);
}

// === helper para extraer el userId del contexto de seguridad ===
private Long currentUserId() {
    try {
        var auth = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null) {
            Object p = auth.getPrincipal();
            // Si se usa un CustomUserDetails con getId():
            try { return (Long) p.getClass().getMethod("getId").invoke(p); }
            catch (Exception ignored) {}
            // Si el username es el id:
            if (p instanceof org.springframework.security.core.userdetails.User u) {
                return Long.valueOf(u.getUsername());
            }
        }
    } catch (Exception ignored) {}
    return 1L;
}


public class EspacioController extends BaseControllerImpl <Espacio, EspacioServiceImpl> {
    
}
