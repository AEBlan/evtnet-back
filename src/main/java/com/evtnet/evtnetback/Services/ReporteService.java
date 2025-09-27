package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Espacio;
import com.evtnet.evtnetback.Repositories.EspacioRepository;
import com.evtnet.evtnetback.Repositories.ReporteRepository;
import com.evtnet.evtnetback.dto.reportes.DTOReportePersonsasEnEventosEnEspacio;
import com.evtnet.evtnetback.util.CurrentUser; // ajusta el paquete si difiere
import lombok.RequiredArgsConstructor;
import com.evtnet.evtnetback.dto.reportes.DatoLocal;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final EspacioRepository espacioRepository;

    public DTOReportePersonsasEnEventosEnEspacio generarPersonasEnEventosEnEspacio(
            Long espacioId, long fechaDesdeMs, long fechaHastaMs) throws Exception {

        if (espacioId == null) throw new IllegalArgumentException("espacioId es requerido");
        if (fechaDesdeMs <= 0 || fechaHastaMs <= 0) throw new IllegalArgumentException("Fechas inválidas");
        if (fechaDesdeMs >= fechaHastaMs) throw new IllegalArgumentException("El rango de fechas es inválido");

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("No autenticado"));
        Espacio espacio = espacioRepository.findById(espacioId)
                .orElseThrow(() -> new IllegalArgumentException("Espacio no encontrado"));
        if (espacio.getPropietario() == null
                || espacio.getPropietario().getUsername() == null
                || !username.equals(espacio.getPropietario().getUsername())) {
            throw new SecurityException("No posee permisos para este espacio");
        }

        ZoneId tz = ZoneId.systemDefault();
        LocalDateTime desde = LocalDateTime.ofInstant(Instant.ofEpochMilli(fechaDesdeMs), tz);
        LocalDateTime hasta = LocalDateTime.ofInstant(Instant.ofEpochMilli(fechaHastaMs), tz);

        List<DatoLocal> filas = reporteRepository.reportePersonasPorEventoEnEspacio(espacioId, desde, hasta);

        List<DTOReportePersonsasEnEventosEnEspacio.Dato> datos = filas.stream()
                .map(r -> DTOReportePersonsasEnEventosEnEspacio.Dato.builder()
                        .evento(r.getEvento())
                        .fechaDesde(r.getFechaDesde().atZone(tz).toInstant())
                        .fechaHasta(r.getFechaHasta().atZone(tz).toInstant())
                        .participantes(r.getParticipantes())
                        .build())
                .toList();

        return DTOReportePersonsasEnEventosEnEspacio.builder()
                .fechaHoraGeneracion(Instant.now())
                .datos(datos)
                .build();
    }
}
