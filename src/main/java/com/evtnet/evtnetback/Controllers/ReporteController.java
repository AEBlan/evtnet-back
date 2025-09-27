package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Services.ReporteService;
import com.evtnet.evtnetback.dto.reportes.DTOReportePersonsasEnEventosEnEspacio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    // GET /reportes/generarPersonasEnEventosEnEspacio?espacioId=...&fechaDesde=...&fechaHasta=...
    @GetMapping("/generarPersonasEnEventosEnEspacio")
    public ResponseEntity<DTOReportePersonsasEnEventosEnEspacio> generarPersonasEnEventosEnEspacio(
            @RequestParam Long espacioId,
            @RequestParam("fechaDesde") long fechaDesdeMs,
            @RequestParam("fechaHasta") long fechaHastaMs
    ) throws Exception {
        DTOReportePersonsasEnEventosEnEspacio dto =
                reporteService.generarPersonasEnEventosEnEspacio(espacioId, fechaDesdeMs, fechaHastaMs);
        return ResponseEntity.ok(dto);
    }
}
