package com.evtnet.evtnetback.controller;

import com.evtnet.evtnetback.service.ReporteService;
import com.evtnet.evtnetback.dto.reportes.*;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    /* 

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
    }*/

    @GetMapping("/generarPersonasEnEventosEnEspacio")
    public ResponseEntity<DTOReportePersonasEnEventosEnEspacio> generarPersonasEnEventosEnEspacio(
            @RequestParam Long espacioId,
            @RequestParam(required = false) Long subespacioId,
            @RequestParam("fechaDesde") long fechaDesdeMs,
            @RequestParam("fechaHasta") long fechaHastaMs
    ) throws Exception {
        DTOReportePersonasEnEventosEnEspacio dto =
                reporteService.generarPersonasEnEventosEnEspacio(espacioId,subespacioId, fechaDesdeMs, fechaHastaMs);
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

    // ===============================================================
    //  Reporte: Participantes por Rango Temporal
    // ===============================================================
    @GetMapping("/generarParticipantesPorRangoTemporal")
    public ResponseEntity<DTOReporteParticipantesPorRangoTemporal> generarParticipantesPorRangoTemporal(
            @RequestParam(name = "todosLosEspacios", defaultValue = "false") boolean todosLosEspacios,
            @RequestParam(name = "espacios", required = false) List<Long> espaciosIds,
            @RequestParam(name = "fechaDesde") long fechaDesdeMs,
            @RequestParam(name = "fechaHasta") long fechaHastaMs,
            @RequestParam(name = "anios", defaultValue = "0") int anios,
            @RequestParam(name = "meses", defaultValue = "0") int meses,
            @RequestParam(name = "dias", defaultValue = "0") int dias,
            @RequestParam(name = "horas", defaultValue = "0") int horas,
            @RequestParam(name = "porSubespacio", defaultValue = "false") boolean porSubespacio,
            Authentication auth
    ) throws Exception {
        // 🔹 Obtener username del usuario autenticado
        String username = auth.getName();

        // 🔹 Llamar al servicio
        DTOReporteParticipantesPorRangoTemporal dto = reporteService.generarParticipantesPorRangoTemporal(
                todosLosEspacios,
                espaciosIds,
                fechaDesdeMs,
                fechaHastaMs,
                anios,
                meses,
                dias,
                horas,
                porSubespacio,
                username
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/generarRegistracionesIniciosSesion")
    public ResponseEntity<DTOReporteRegistracionesIniciosSesion> generarRegistracionesIniciosSesion(
            @RequestParam long fechaDesde,
            @RequestParam long fechaHasta,
            @RequestParam(defaultValue = "0") int anios,
            @RequestParam(defaultValue = "0") int meses,
            @RequestParam(defaultValue = "7") int dias,
            @RequestParam(defaultValue = "0") int horas
    ) throws Exception {
        var dto = reporteService.generarReporteRegistracionesIniciosSesion(
                fechaDesde, fechaHasta, anios, meses, dias, horas
        );
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/generarTiempoMedioMonetizacion")
    public ResponseEntity<DTOReporteTiempoMedioMonetizacion> generarTiempoMedioMonetizacion(
            @RequestParam("fechaDesde") Long fechaDesdeMillis,
            @RequestParam("fechaHasta") Long fechaHastaMillis,
            @RequestParam int anios,
            @RequestParam int meses,
            @RequestParam int dias,
            @RequestParam int horas
    ) {

        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime fechaDesde = Instant.ofEpochMilli(fechaDesdeMillis)
                .atZone(zone).toLocalDateTime();
        LocalDateTime fechaHasta = Instant.ofEpochMilli(fechaHastaMillis)
                .atZone(zone).toLocalDateTime();

        DTOReporteTiempoMedioMonetizacion dto =
                reporteService.generarTiempoMedioMonetizacion(
                        fechaDesde, fechaHasta, anios, meses, dias, horas
                );

        return ResponseEntity.ok(dto);
    }
}
