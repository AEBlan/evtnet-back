package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.AdministradorEspacio;
import com.evtnet.evtnetback.Entities.Espacio;
import com.evtnet.evtnetback.Repositories.EspacioRepository;
import com.evtnet.evtnetback.Repositories.ReporteRepository;
import com.evtnet.evtnetback.dto.reportes.*;
import com.evtnet.evtnetback.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final EspacioRepository espacioRepository;

    public DTOReporteEventosPorEspacio generarEventosPorEspacio(
            List<Long> espaciosIds, long fechaDesdeMs, long fechaHastaMs) throws Exception {

        if (espaciosIds == null || espaciosIds.isEmpty())
            throw new IllegalArgumentException("Debe seleccionar al menos un espacio");
        if (fechaDesdeMs >= fechaHastaMs)
            throw new IllegalArgumentException("El rango de fechas es inválido");

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("No autenticado"));

        List<Espacio> espacios = espacioRepository.findAllById(espaciosIds);
        if (espacios.size() != new HashSet<>(espaciosIds).size())
            throw new IllegalArgumentException("Alguno de los espacios no existe");

        // 🔹 Verificar que todos pertenezcan al usuario actual
        boolean todosPropios = espacios.stream().allMatch(e ->
                e.getAdministradoresEspacio().stream()
                        .anyMatch(ae -> ae.getTipoAdministradorEspacio() != null &&
                                ae.getTipoAdministradorEspacio().getNombre().equalsIgnoreCase("PROPIETARIO") &&
                                ae.getUsuario() != null &&
                                ae.getUsuario().getUsername().equalsIgnoreCase(username))
        );

        if (!todosPropios)
            throw new SecurityException("No posee permisos sobre los espacios seleccionados");

        ZoneId tz = ZoneId.systemDefault();
        Instant iDesde = Instant.ofEpochMilli(fechaDesdeMs);
        Instant iHasta = Instant.ofEpochMilli(fechaHastaMs);
        LocalDateTime desde = LocalDateTime.ofInstant(iDesde, tz);
        LocalDateTime hasta = LocalDateTime.ofInstant(iHasta, tz);

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

    public DTOReporteParticipantesPorRangoTemporal generarParticipantesPorRangoTemporal(
            boolean todosLosEspacios,
            List<Long> espaciosIds,
            long fechaDesdeMs,
            long fechaHastaMs,
            int anios,
            int meses,
            int dias,
            int horas,
            String username
    ) throws Exception {

        if (fechaDesdeMs >= fechaHastaMs)
            throw new IllegalArgumentException("Rango de fechas inválido");

        ZoneId tz = ZoneId.systemDefault();
        LocalDateTime desde = LocalDateTime.ofInstant(Instant.ofEpochMilli(fechaDesdeMs), tz);
        LocalDateTime hasta = LocalDateTime.ofInstant(Instant.ofEpochMilli(fechaHastaMs), tz);

        Period periodo = Period.of(anios, meses, dias);
        Duration duracion = Duration.ofHours(horas);
        List<LocalDateTime[]> intervalos = new ArrayList<>();
        LocalDateTime cursor = desde;
        while (cursor.isBefore(hasta)) {
            LocalDateTime next = cursor.plus(periodo).plus(duracion);
            if (next.isAfter(hasta)) next = hasta;
            intervalos.add(new LocalDateTime[]{cursor, next});
            cursor = next;
        }

        List<Espacio> espacios = todosLosEspacios
                ? espacioRepository.findByPropietarioUsername(username)
                : espacioRepository.findAllById(espaciosIds);

        List<DTOReporteParticipantesPorRangoTemporal.Dato> datos = new ArrayList<>();

        for (Espacio e : espacios) {
            for (LocalDateTime[] par : intervalos) {
                long count = reporteRepository.contarParticipantesPorRango(
                        e.getId(), par[0], par[1]);
                datos.add(DTOReporteParticipantesPorRangoTemporal.Dato.builder()
                        .espacio(e.getNombre())
                        .fechaDesde(par[0].atZone(tz).toInstant())
                        .fechaHasta(par[1].atZone(tz).toInstant())
                        .participantes(count)
                        .build());
            }
        }

        return DTOReporteParticipantesPorRangoTemporal.builder()
                .fechaHoraGeneracion(Instant.now())
                .datos(datos)
                .build();
    }
    public DTOReportePersonsasEnEventosEnEspacio generarPersonasEnEventosEnEspacio(
        Long espacioId, long fechaDesdeMs, long fechaHastaMs) throws Exception {

        if (espacioId == null)
                throw new IllegalArgumentException("espacioId es requerido");
        if (fechaDesdeMs >= fechaHastaMs)
                throw new IllegalArgumentException("El rango de fechas es inválido");

        var espacio = espacioRepository.findById(espacioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espacio no encontrado"));

        ZoneId tz = ZoneId.systemDefault();
        LocalDateTime desde = LocalDateTime.ofInstant(Instant.ofEpochMilli(fechaDesdeMs), tz);
        LocalDateTime hasta = LocalDateTime.ofInstant(Instant.ofEpochMilli(fechaHastaMs), tz);

        var filas = reporteRepository.reportePersonasPorEventoEnEspacio(espacioId, desde, hasta);

        var datos = filas.stream()
                .map(f -> DTOReportePersonsasEnEventosEnEspacio.Dato.builder()
                        .evento(f.getEvento())
                        .fechaDesde(f.getFechaDesde().atZone(tz).toInstant())
                        .fechaHasta(f.getFechaHasta().atZone(tz).toInstant())
                        .participantes(f.getParticipantes())
                        .build())
                .toList();

        return DTOReportePersonsasEnEventosEnEspacio.builder()
                .fechaHoraGeneracion(Instant.now())
                .datos(datos)
                .build();
        }

}
