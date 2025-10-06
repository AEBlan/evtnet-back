package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Espacio;
import com.evtnet.evtnetback.Repositories.EspacioRepository;
import com.evtnet.evtnetback.Repositories.ReporteRepository;
import com.evtnet.evtnetback.dto.reportes.*;
import com.evtnet.evtnetback.util.CurrentUser; // ajusta el paquete si difiere
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public DTOReporteEventosPorEspacio generarEventosPorEspacio(
            List<Long> espaciosIds, long fechaDesdeMs, long fechaHastaMs) throws Exception {

        if (espaciosIds == null || espaciosIds.isEmpty())
            throw new IllegalArgumentException("Debe seleccionar al menos un espacio");
        if (fechaDesdeMs >= fechaHastaMs)
            throw new IllegalArgumentException("El rango de fechas es inválido");

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("No autenticado"));

        // Verificamos que todos los espacios existan y sean del usuario actual
        List<Espacio> espacios = espacioRepository.findAllById(espaciosIds);
        if (espacios.size() != new HashSet<>(espaciosIds).size())
            throw new IllegalArgumentException("Alguno de los espacios no existe");

        boolean todosPropios = espacios.stream().allMatch(e ->
                e.getPropietario() != null &&
                username.equals(e.getPropietario().getUsername())
        );
        if (!todosPropios)
            throw new SecurityException("No posee permisos sobre los espacios seleccionados");

        ZoneId tz = ZoneId.systemDefault();
        Instant iDesde = Instant.ofEpochMilli(fechaDesdeMs);
        Instant iHasta = Instant.ofEpochMilli(fechaHastaMs);
        LocalDateTime desde = LocalDateTime.ofInstant(iDesde, tz);
        LocalDateTime hasta = LocalDateTime.ofInstant(iHasta, tz);

        // Query de conteo
        var filas = reporteRepository.contarEventosPorEspacio(espaciosIds, desde, hasta);

        Map<Long, Long> conteos = filas.stream()
                .collect(Collectors.toMap(
                        ReporteRepository.RowEventosPorEspacio::getEspacioId,
                        ReporteRepository.RowEventosPorEspacio::getEventos
                ));

        List<DTOReporteEventosPorEspacio.Dato> datos = espacios.stream()
                .map(e -> DTOReporteEventosPorEspacio.Dato.builder()
                        .espacio(e.getNombre())
                        .fechaDesde(iDesde)
                        .fechaHasta(iHasta)
                        .eventos(conteos.getOrDefault(e.getId(), 0L))
                        .build())
                .sorted(Comparator
                        .comparingLong(DTOReporteEventosPorEspacio.Dato::getEventos).reversed()
                        .thenComparing(DTOReporteEventosPorEspacio.Dato::getEspacio))
                .toList();

        return DTOReporteEventosPorEspacio.builder()
                .fechaHoraGeneracion(Instant.now())
                .datos(datos)
                .build();
    }
}
