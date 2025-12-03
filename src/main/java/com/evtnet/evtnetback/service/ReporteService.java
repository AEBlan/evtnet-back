package com.evtnet.evtnetback.service;


import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.repository.*;
import com.evtnet.evtnetback.dto.reportes.*;
import com.evtnet.evtnetback.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
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

                        //  Validar usuario autenticado
                        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("No autenticado"));

                        //  Verificar existencia de los espacios
                        List<Espacio> espacios = espacioRepository.findAllById(espaciosIds);
                        if (espacios.size() != new HashSet<>(espaciosIds).size())
                                throw new IllegalArgumentException("Alguno de los espacios no existe.");

                        //  Verificar propiedad (todos deben ser del usuario)
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

                public DTOReporteParticipantesPorRangoTemporal generarParticipantesPorRangoTemporal(
                boolean todosLosEspacios,
                List<Long> espaciosIds,
                LocalDateTime fechaDesde,
                LocalDateTime fechaHasta,
                int anios,
                int meses,
                int dias,
                int horas
        ) {

        // 1️⃣ Obtener espacios
        List<Espacio> espacios = todosLosEspacios
                ? espacioRepository.findAll()
                : espacioRepository.findAllById(espaciosIds);

        DTOReporteParticipantesPorRangoTemporal dto = new DTOReporteParticipantesPorRangoTemporal();
        dto.setFechaHoraGeneracion(Instant.now());

        List<DTOReporteParticipantesPorRangoTemporal.Entrada> entradas = new ArrayList<>();

        long totalGeneral = 0;  // ⭐ Acumulado para "Todos los espacios"

        for (Espacio esp : espacios) {

                for (SubEspacio sub : esp.getSubEspacios()) {

                // ⭐ Cantidad total para ese subespacio
                Long cantidad = reporteRepository.contarParticipantesEnRangoPorSubespacio(
                        sub.getId(),
                        fechaDesde,
                        fechaHasta
                );
                if (cantidad == null) cantidad = 0L;

                totalGeneral += cantidad;

                // ⭐ Crear entrada normal
                DTOReporteParticipantesPorRangoTemporal.Entrada entrada =
                        new DTOReporteParticipantesPorRangoTemporal.Entrada();

                entrada.setEspacio(esp.getNombre() + " - " + sub.getNombre());

                DTOReporteParticipantesPorRangoTemporal.Rango rango =
                        new DTOReporteParticipantesPorRangoTemporal.Rango();

                rango.setInicio(fechaDesde.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                rango.setFin(fechaHasta.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                rango.setParticipantes(cantidad);

                entrada.setRangos(List.of(rango));
                entradas.add(entrada);
                }
        }

        // 2️⃣ Si pidió "todos los espacios", agregamos un resumen
        if (todosLosEspacios) {
                DTOReporteParticipantesPorRangoTemporal.Entrada entradaTotal =
                        new DTOReporteParticipantesPorRangoTemporal.Entrada();

                entradaTotal.setEspacio("Todos los espacios");

                DTOReporteParticipantesPorRangoTemporal.Rango rangoTotal =
                        new DTOReporteParticipantesPorRangoTemporal.Rango();

                rangoTotal.setInicio(fechaDesde.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                rangoTotal.setFin(fechaHasta.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                rangoTotal.setParticipantes(totalGeneral);

                entradaTotal.setRangos(List.of(rangoTotal));
                entradas.add(entradaTotal);
        }

        dto.setDatos(entradas);
        return dto;
        }

       // 🔹 Reporte: Registraciones e inicios de sesión
        public DTOReporteRegistracionesIniciosSesion generarReporteRegistracionesIniciosSesion(
                long fechaDesdeMs,
                long fechaHastaMs,
                int anios,
                int meses,
                int dias,
                int horas
        ) throws Exception {

                if (fechaDesdeMs >= fechaHastaMs)
                throw new IllegalArgumentException("Ingrese el rango de fechas");

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

                // 📂 Lectura de logs CSV
                Path logDir = Paths.get("storage/logs/UsuariosGrupos");
                if (!Files.exists(logDir)) {
                throw new RuntimeException("No se encontró el directorio de logs: " + logDir.toAbsolutePath());
                }

                List<RegistroLog> eventos = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                try (var archivos = Files.list(logDir)) {
                archivos.filter(p -> p.toString().endsWith(".csv"))
                        .forEach(path -> {
                                try (BufferedReader reader = Files.newBufferedReader(path)) {
                                String linea;
                                boolean primera = true;
                                while ((linea = reader.readLine()) != null) {
                                        if (primera) {
                                        primera = false;
                                        continue;
                                        }
                                        String[] cols = linea.replace("\"", "").split(",");
                                        if (cols.length < 3) continue;

                                        String tipo = cols[0].trim();
                                        String fechaTexto = cols[2].trim();

                                        LocalDateTime fecha;
                                        try {
                                        fecha = LocalDateTime.parse(fechaTexto, formatter);
                                        } catch (Exception e) {
                                        continue; // si hay formato incorrecto, lo saltamos
                                        }

                                        eventos.add(new RegistroLog(tipo, fecha));
                                }
                                } catch (Exception e) {
                                System.err.println("Error leyendo log CSV: " + path + " - " + e.getMessage());
                                }
                        });
                }

                // 🔹 Agrupar resultados por intervalos
                List<DTOReporteRegistracionesIniciosSesion.Dato> datos = new ArrayList<>();

                for (LocalDateTime[] rango : intervalos) {
                long regs = eventos.stream()
                        .filter(e -> e.tipo.equalsIgnoreCase("registro"))
                        .filter(e -> !e.fecha.isBefore(rango[0]) && e.fecha.isBefore(rango[1]))
                        .count();

                long logins = eventos.stream()
                        .filter(e -> e.tipo.equalsIgnoreCase("inicio_sesion"))
                        .filter(e -> !e.fecha.isBefore(rango[0]) && e.fecha.isBefore(rango[1]))
                        .count();

                double proporcion = regs > 0 ? (double) logins / regs : 0.0;

                datos.add(DTOReporteRegistracionesIniciosSesion.Dato.builder()
                        .inicio(rango[0].atZone(tz).toInstant())
                        .fin(rango[1].atZone(tz).toInstant())
                        .registraciones(regs)
                        .iniciosSesion(logins)
                        .proporcion(proporcion)
                        .build());
                }

                if (datos.isEmpty())
                throw new NoSuchElementException("No se encontraron datos en el rango de fechas indicado");

                return DTOReporteRegistracionesIniciosSesion.builder()
                        .fechaHoraGeneracion(Instant.now())
                        .datos(datos)
                        .build();
        }

        // Clase interna simple para representar un evento
        private record RegistroLog(String tipo, LocalDateTime fecha) {}


        public DTOReporteTiempoMedioMonetizacion generarTiempoMedioMonetizacion(
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta,
        int anios,
        int meses,
        int dias,
        int horas
                ) {

                Agrupacion agrupacion = determinarAgrupacion(anios, meses, dias, horas);
                List<Rango> rangos = generarRangos(fechaDesde, fechaHasta, agrupacion);

                DTOReporteTiempoMedioMonetizacion dto = new DTOReporteTiempoMedioMonetizacion();
                dto.setFechaHoraGeneracion(LocalDateTime.now());

                List<DTOReporteTiempoMedioMonetizacion.Item> items = new ArrayList<>();

                for (Rango r : rangos) {

                        BigDecimal montoIns = reporteRepository.obtenerIngresosPorInscripcion(r.inicio(), r.fin());
                        BigDecimal montoOrg = reporteRepository.obtenerIngresosPorOrganizacion(r.inicio(), r.fin());
                        BigDecimal montoCuota = reporteRepository.obtenerCuotaPorEspacio(r.inicio(), r.fin());

                        DTOReporteTiempoMedioMonetizacion.Item item = new DTOReporteTiempoMedioMonetizacion.Item();
                        item.setInicio(r.inicio());
                        item.setFin(r.fin());

                        List<DTOReporteTiempoMedioMonetizacion.Medio> medios = new ArrayList<>();
                        medios.add(crearMedio("Comisión por inscripción", montoIns));
                        medios.add(crearMedio("Comisión por organización", montoOrg));
                        medios.add(crearMedio("Cuota por uso del espacio", montoCuota));

                        item.setMedios(medios);
                        items.add(item);
                }

                dto.setDatos(items);
                return dto;
                }

                private DTOReporteTiempoMedioMonetizacion.Medio crearMedio(String nombre, BigDecimal monto) {
                DTOReporteTiempoMedioMonetizacion.Medio medio = new DTOReporteTiempoMedioMonetizacion.Medio();
                medio.setNombre(nombre);
                medio.setMonto(monto != null ? monto.doubleValue() : 0.0);
                return medio;
                }


        // ─────────────────────────────────────────────────────────────
        // AGRUPACIÓN (HORA / DÍA / MES)
        // ─────────────────────────────────────────────────────────────

        private enum Agrupacion { HORA, DIA, MES }

        private Agrupacion determinarAgrupacion(int anios, int meses, int dias, int horas) {
                if (horas > 0) return Agrupacion.HORA;
                if (dias > 0) return Agrupacion.DIA;
                return Agrupacion.MES;
        }

        // ─────────────────────────────────────────────────────────────
        // GENERACIÓN DE RANGOS
        // ─────────────────────────────────────────────────────────────

        private List<Rango> generarRangos(LocalDateTime inicio, LocalDateTime fin, Agrupacion agrupacion) {
                List<Rango> lista = new ArrayList<>();
                LocalDateTime cursor = inicio;

                while (!cursor.isAfter(fin)) {
                LocalDateTime siguiente;

                switch (agrupacion) {
                        case HORA -> siguiente = cursor.plusHours(1).minusSeconds(1);
                        case DIA -> siguiente = cursor.toLocalDate().atTime(23, 59, 59);
                        case MES -> {
                        LocalDateTime firstOfMonth = cursor.withDayOfMonth(1)
                                .withHour(0).withMinute(0).withSecond(0).withNano(0);
                        LocalDateTime lastOfMonth = firstOfMonth.plusMonths(1).minusSeconds(1);
                        siguiente = lastOfMonth;
                        }
                        default -> throw new IllegalStateException("Agrupación no soportada");
                }

                if (siguiente.isAfter(fin)) {
                        siguiente = fin;
                }

                lista.add(new Rango(cursor, siguiente));
                cursor = siguiente.plusSeconds(1);
                }

                return lista;
        }

        private record Rango(LocalDateTime inicio, LocalDateTime fin) {}

        


}
