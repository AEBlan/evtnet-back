package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Espacio;
import com.evtnet.evtnetback.repository.EspacioRepository;
import com.evtnet.evtnetback.repository.ReporteRepository;
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

   /*

    // ===============================================================
    //  Reporte: Participantes por rango temporal
    // ===============================================================
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

        // Crear los intervalos de tiempo
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

        // Espacios a incluir (todos los del usuario o los seleccionados)
        List<Espacio> espacios = todosLosEspacios
                ? espacioRepository.findByPropietarioUsername(username)
                : espacioRepository.findAllById(espaciosIds);

        List<DTOReporteParticipantesPorRangoTemporal.Dato> datos = new ArrayList<>();

        for (Espacio e : espacios) {
            for (LocalDateTime[] par : intervalos) {
                // 🔹 Cuenta inscripciones activas en eventos de todos los subespacios del espacio
                long count = reporteRepository.contarParticipantesPorRango(e.getId(), par[0], par[1]);
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
    }*/




    //Actualizados
    public DTOReportePersonasEnEventosEnEspacio generarPersonasEnEventosEnEspacio(
            Long espacioId,
            Long subespacioId,
            long fechaDesdeMs,
            long fechaHastaMs) throws Exception {

        if (espacioId == null)
            throw new Exception("El parámetro 'espacioId' es requerido.");

        if (fechaDesdeMs >= fechaHastaMs)
            throw new Exception("El rango de fechas es inválido.");

        var espacio = espacioRepository.findById(espacioId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Espacio no encontrado"));

        // 🔹 Conversión de milisegundos a LocalDateTime
        ZoneId tz = ZoneId.systemDefault();
        LocalDateTime desde = LocalDateTime.ofInstant(Instant.ofEpochMilli(fechaDesdeMs), tz);
        LocalDateTime hasta = LocalDateTime.ofInstant(Instant.ofEpochMilli(fechaHastaMs), tz);

        // 🔹 Consulta al repositorio: si subespacioId es null, cuenta todos los subespacios del espacio
        var filas = reporteRepository.reportePersonasPorEventoEnEspacio(
                espacioId, subespacioId, desde, hasta);

        if (filas == null || filas.isEmpty()) {
            throw new Exception("No se encontraron eventos en el rango de fechas indicado.");
        }

        // 🔹 Mapeo a DTO
        var datos = filas.stream()
                .map(f -> DTOReportePersonasEnEventosEnEspacio.Dato.builder()
                        .evento(f.getEvento())
                        .fechaDesde(f.getFechaDesde().atZone(tz).toInstant())
                        .fechaHasta(f.getFechaHasta().atZone(tz).toInstant())
                        .participantes(f.getParticipantes())
                        .build())
                .sorted(Comparator
                        .comparingLong(DTOReportePersonasEnEventosEnEspacio.Dato::getParticipantes).reversed()
                        .thenComparing(DTOReportePersonasEnEventosEnEspacio.Dato::getEvento))
                .toList();

        // 🔹 Devuelve el DTO completo
        return DTOReportePersonasEnEventosEnEspacio.builder()
                .fechaHoraGeneracion(Instant.now())
                .datos(datos)
                .build();
    }
     
    
    
    public DTOReporteEventosPorEspacio generarEventosPorEspacio(
        List<Long> espaciosIds, long fechaDesdeMs, long fechaHastaMs) throws Exception {

        if (espaciosIds == null || espaciosIds.isEmpty())
                throw new IllegalArgumentException("Debe seleccionar al menos un espacio.");
        if (fechaDesdeMs >= fechaHastaMs)
                throw new IllegalArgumentException("El rango de fechas es inválido.");

        // 🔐 Validar usuario autenticado
        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("No autenticado"));

        // 🔎 Verificar existencia de los espacios
        List<Espacio> espacios = espacioRepository.findAllById(espaciosIds);
        if (espacios.size() != new HashSet<>(espaciosIds).size())
                throw new IllegalArgumentException("Alguno de los espacios no existe.");

        // 🔎 Verificar propiedad (todos deben ser del usuario)
        boolean todosPropios = espacios.stream().allMatch(e ->
                e.getAdministradoresEspacio().stream()
                        .anyMatch(ae -> ae.getTipoAdministradorEspacio() != null &&
                                ae.getTipoAdministradorEspacio().getNombre().equalsIgnoreCase("PROPIETARIO") &&
                                ae.getUsuario() != null &&
                                ae.getUsuario().getUsername().equalsIgnoreCase(username))
        );

        if (!todosPropios)
                throw new SecurityException("No posee permisos sobre los espacios seleccionados.");

        // 🕒 Convertir fechas
        ZoneId tz = ZoneId.systemDefault();
        Instant iDesde = Instant.ofEpochMilli(fechaDesdeMs);
        Instant iHasta = Instant.ofEpochMilli(fechaHastaMs);
        LocalDateTime desde = LocalDateTime.ofInstant(iDesde, tz);
        LocalDateTime hasta = LocalDateTime.ofInstant(iHasta, tz);

        // 📊 Consultar eventos por subespacio
        var filas = reporteRepository.contarEventosPorSubespaciosDeEspacios(espaciosIds, desde, hasta);

        if (filas.isEmpty())
                throw new NoSuchElementException("No se encontraron eventos en el rango de fechas indicado.");

        // 🧩 Mapear resultados al DTO (subespacios como “espacio” visual)
        var datos = filas.stream()
                .map(f -> DTOReporteEventosPorEspacio.Dato.builder()
                        .espacio(f.getEspacio() + " - " + f.getSubespacio())  // Ej: "Club Municipal - Cancha 1"
                        .fechaDesde(iDesde)
                        .fechaHasta(iHasta)
                        .eventos(f.getEventos())
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

        // ===============================================================
        //  Reporte: Participantes por rango temporal
        //  👉 Puede agrupar por subespacio o sumar por espacio completo
        // ===============================================================
        public DTOReporteParticipantesPorRangoTemporal generarParticipantesPorRangoTemporal(
                boolean todosLosEspacios,
                List<Long> espaciosIds,
                long fechaDesdeMs,
                long fechaHastaMs,
                int anios,
                int meses,
                int dias,
                int horas,
                boolean porSubespacio,      
                String username
        ) throws Exception {

        if (fechaDesdeMs >= fechaHastaMs)
                throw new IllegalArgumentException("Rango de fechas inválido");

        ZoneId tz = ZoneId.systemDefault();
        LocalDateTime desde = LocalDateTime.ofInstant(Instant.ofEpochMilli(fechaDesdeMs), tz);
        LocalDateTime hasta = LocalDateTime.ofInstant(Instant.ofEpochMilli(fechaHastaMs), tz);

        // 🔹 Crear los intervalos de tiempo
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

        // 🔹 Espacios del usuario o seleccionados manualmente
        List<Espacio> espacios = todosLosEspacios
                ? espacioRepository.findByPropietarioUsername(username)
                : espacioRepository.findAllById(espaciosIds);

        List<DTOReporteParticipantesPorRangoTemporal.Dato> datos = new ArrayList<>();

        for (Espacio e : espacios) {
                for (LocalDateTime[] par : intervalos) {

                if (porSubespacio) {
                        // 🔸 Detalle por subespacio
                        var filas = reporteRepository.contarParticipantesPorRango(e.getId(), par[0], par[1]);

                        for (ReporteRepository.RowParticipantesPorSubespacio f : filas) {
                        datos.add(DTOReporteParticipantesPorRangoTemporal.Dato.builder()
                                .espacio(e.getNombre())
                                .subespacio(f.getSubespacio())
                                .fechaDesde(par[0].atZone(tz).toInstant())
                                .fechaHasta(par[1].atZone(tz).toInstant())
                                .participantes(f.getParticipantes())
                                .build());
                        }

                } else {
                        // 🔹 Total por espacio (suma de todos sus subespacios)
                        var filas = reporteRepository.contarParticipantesPorRango(e.getId(), par[0], par[1]);
                        long total = filas.stream().mapToLong(ReporteRepository.RowParticipantesPorSubespacio::getParticipantes).sum();

                        datos.add(DTOReporteParticipantesPorRangoTemporal.Dato.builder()
                                .espacio(e.getNombre())
                                .subespacio("Todos los subespacios") // etiqueta genérica
                                .fechaDesde(par[0].atZone(tz).toInstant())
                                .fechaHasta(par[1].atZone(tz).toInstant())
                                .participantes(total)
                                .build());
                }
                }
        }

        return DTOReporteParticipantesPorRangoTemporal.builder()
                .fechaHoraGeneracion(Instant.now())
                .datos(datos)
                .build();
        }


}
