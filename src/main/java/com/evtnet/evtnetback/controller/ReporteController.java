package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.service.ReporteService;
import com.evtnet.evtnetback.dto.reportes.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;


import java.util.List;

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

    @GetMapping("/generarEventosPorEspacio")
    public ResponseEntity<DTOReporteEventosPorEspacio> generarEventosPorEspacio(
            @RequestParam List<Long> espacios,
            @RequestParam("fechaDesde") long fechaDesdeMs,
            @RequestParam("fechaHasta") long fechaHastaMs
    ) throws Exception {
        var dto = reporteService.generarEventosPorEspacio(espacios, fechaDesdeMs, fechaHastaMs);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/generarParticipantesPorRangoTemporal")
    public ResponseEntity<DTOReporteParticipantesPorRangoTemporal> generarParticipantesPorRangoTemporal(
            @RequestParam boolean todosLosEspacios,
            @RequestParam(required = false) List<Long> espacios,
            @RequestParam long fechaDesde,
            @RequestParam long fechaHasta,
            @RequestParam int anios,
            @RequestParam int meses,
            @RequestParam int dias,
            @RequestParam int horas,
            Authentication auth
    ) throws Exception {
        return ResponseEntity.ok(reporteService.generarParticipantesPorRangoTemporal(
                todosLosEspacios, espacios, fechaDesde, fechaHasta, anios, meses, dias, horas, auth.getName()
        ));
    }

}
