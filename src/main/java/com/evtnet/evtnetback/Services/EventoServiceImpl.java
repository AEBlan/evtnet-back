// src/main/java/com/evtnet/evtnetback/Services/EventoServiceImpl.java
package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.*;
import com.evtnet.evtnetback.Repositories.*;
import com.evtnet.evtnetback.Repositories.specs.EventoSpecs;
import com.evtnet.evtnetback.dto.disciplinaevento.DTODisciplinaEventoCreate;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;
import com.evtnet.evtnetback.dto.usuarios.DTOPreferenciaPago;
import com.evtnet.evtnetback.error.HttpErrorException;
import com.evtnet.evtnetback.mapper.EventoSearchMapper;
import com.evtnet.evtnetback.util.CurrentUser;
import com.evtnet.evtnetback.util.MercadoPagoSingleton;
import com.evtnet.evtnetback.dto.eventos.DTOEventoDetalle;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.evtnet.evtnetback.Repositories.specs.DenunciaEventoSpecs;

import org.springframework.beans.factory.annotation.Value;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import com.evtnet.evtnetback.utils.TimeUtil;



import java.math.RoundingMode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class EventoServiceImpl extends BaseServiceImpl<Evento, Long> implements EventoService { 

    private final EventoRepository eventoRepo;
    private final DisciplinaEventoRepository disciplinaEventoRepo;
    private final DisciplinaRepository disciplinaBaseRepo;
    private final EspacioRepository espacioRepo;
    private final InscripcionRepository inscripcionRepo;
    private final AdministradorEventoRepository administradorEventoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ComprobantePagoRepository comprobanteRepo;
    private final InvitadoRepository invitadoRepo;
    private final DenunciaEventoRepository denunciaEventoRepo;
    private final EstadoDenunciaEventoRepository estadoDenunciaRepo;
    private final DenunciaEventoEstadoRepository denunciaEventoEstadoRepo;
    private final SuperEventoRepository superEventoRepo; // 👈 Agregar esto
    private final MercadoPagoSingleton mercadoPagoSingleton;
    private final ParametroSistemaRepository parametroRepo;
    private final TipoAdministradorEventoRepository tipoAdminEventoRepo;
    private static final ZoneId ZONA_ARG = ZoneId.of("America/Argentina/Buenos_Aires");
    private final SubEspacioRepository subEspacioRepo;
    private final DisciplinaSubEspacioRepository disciplinaSubEspacioRepo;
    private final EstadoEventoRepository estadoEventoRepo;
    private final EventoEstadoRepository eventoEstadoRepo;
    private final ParametroSistemaService parametroSistemaService;
    private final ComisionPorInscripcionService comisionPorInscripcionService;

    @Value("${app.timezone:UTC}") // por defecto UTC si no está configurado
    private String appTimezone;

    public EventoServiceImpl(
            EventoRepository eventoRepo,
            DisciplinaEventoRepository disciplinaEventoRepo,
            DisciplinaRepository disciplinaBaseRepo,
            EspacioRepository espacioRepo,
            InscripcionRepository inscripcionRepo,
            AdministradorEventoRepository administradorEventoRepo,
            UsuarioRepository usuarioRepo,
            ComprobantePagoRepository comprobanteRepo,
            InvitadoRepository invitadoRepo,
            DenunciaEventoRepository denunciaEventoRepo,
            EstadoDenunciaEventoRepository estadoDenunciaRepo,
            DenunciaEventoEstadoRepository denunciaEventoEstadoRepo,
            SuperEventoRepository superEventoRepo,
	    MercadoPagoSingleton mercadoPagoSingleton,
	    ParametroSistemaRepository parametroRepo,
	    TipoAdministradorEventoRepository tipoAdminEventoRepo,
            SubEspacioRepository subEspacioRepo,
            DisciplinaSubEspacioRepository disciplinaSubEspacioRepo,
            EstadoEventoRepository estadoEventoRepo,
            EventoEstadoRepository eventoEstadoRepo,
            ParametroSistemaService parametroSistemaService,
            ComisionPorInscripcionService comisionPorInscripcionService

    ) {
        super(eventoRepo);
        this.eventoRepo = eventoRepo;
        this.disciplinaEventoRepo = disciplinaEventoRepo;
        this.disciplinaBaseRepo = disciplinaBaseRepo;
        this.espacioRepo = espacioRepo;
        this.inscripcionRepo = inscripcionRepo;
        this.administradorEventoRepo = administradorEventoRepo;
        this.usuarioRepo = usuarioRepo;
        this.comprobanteRepo = comprobanteRepo;
        this.invitadoRepo = invitadoRepo;
        this.denunciaEventoRepo = denunciaEventoRepo;
        this.estadoDenunciaRepo = estadoDenunciaRepo;
        this.denunciaEventoEstadoRepo = denunciaEventoEstadoRepo;
        this.superEventoRepo = superEventoRepo;
        this.mercadoPagoSingleton = mercadoPagoSingleton;
	this.parametroRepo = parametroRepo;
	this.tipoAdminEventoRepo = tipoAdminEventoRepo;
        this.subEspacioRepo = subEspacioRepo;
        this.disciplinaSubEspacioRepo = disciplinaSubEspacioRepo;
        this.estadoEventoRepo = estadoEventoRepo;
        this.eventoEstadoRepo = eventoEstadoRepo;
        this.parametroSistemaService = parametroSistemaService;
        this.comisionPorInscripcionService = comisionPorInscripcionService;

    }
     
@Override
@Transactional
public List<DTOResultadoBusquedaEventos> buscar(DTOBusquedaEventos filtro) {

    // ==== 0) Parámetros globales ====
    BigDecimal c_u = parametroSistemaService.getDecimal("c_u", new BigDecimal("0.4"));
    BigDecimal c_d = parametroSistemaService.getDecimal("c_d", new BigDecimal("0.35"));
    BigDecimal c_p = parametroSistemaService.getDecimal("c_p", new BigDecimal("0.25"));
    BigDecimal c_e = parametroSistemaService.getDecimal("c_e", new BigDecimal("0.3"));
    BigDecimal max_p = parametroSistemaService.getDecimal("max_p", new BigDecimal("20000"));

    // radio máximo por defecto en metros para la función u (si no mandan ubicación)
    double max_d_m =  paramMaxDistancia(); // ej 5000; ver función abajo

    // ==== 1) Traer candidatos: solo futuros, sin cancelados/rechazados ====
    // Si ya tenés specs: podés usar las tuyas. Yo lo hago en memoria para no tocar tu repo.
    final var ahora = java.time.LocalDateTime.now();
    List<Evento> candidatos = eventoRepo.findAll().stream()
        .filter(e -> e.getFechaHoraInicio() != null && e.getFechaHoraInicio().isAfter(ahora))
        .filter(e -> !estaCanceladoORechazado(e)) // ver helper abajo
        .toList();

    // ==== 2) Aplicar FILTROS EXCLUYENTES ====
    candidatos = aplicarFiltrosExcluyentes(candidatos, filtro, ahora);

    // ==== 3) Calcular SCORE y ordenar ====
    // Centro de búsqueda (si el front lo manda). Si no, u = 0 (queda listo para futura mejora con “últimos 10 eventos”)
    Double lat   = getOrNull(() -> filtro.ubicacion() != null ? filtro.ubicacion().latitud() : null);
    Double lon   = getOrNull(() -> filtro.ubicacion() != null ? filtro.ubicacion().longitud() : null);
    Double radio = getOrNull(() -> filtro.ubicacion() != null ? filtro.ubicacion().rango() : null);

    final double centerLat = lat != null ? lat : 0d;
    final double centerLon = lon != null ? lon : 0d;
    final double maxd = (radio != null && radio > 0) ? radio : max_d_m;

    // disciplinas filtradas (para d)
    final java.util.Set<Long> disciplinasFiltro = getIdsDisciplinasFiltro(filtro); // ver helper

    // precio tope para p (no filtra si no viene; solo ordena)
    final BigDecimal maxPrecioOrden = filtro.precioLimite() != null ? new BigDecimal(filtro.precioLimite()) : max_p;

    record Ranked(Evento e, double score) {}

    List<Ranked> ranked = candidatos.stream()
        .map(e -> {
            final boolean esSE = (e.getSuperEvento() != null);
            final double u = (lat != null && lon != null) ? scoreU(e, centerLat, centerLon, maxd) : 0.0;
            final double d = disciplinasFiltro.isEmpty() ? 0.0 : scoreD(e, disciplinasFiltro);
            final double p = scoreP(precioTotal(e), maxPrecioOrden);
            final double score = esSE ? c_e.doubleValue()*d
                                      : (1.0 - c_e.doubleValue()) * (c_u.doubleValue()*u + c_d.doubleValue()*d + c_p.doubleValue()*p);
            return new Ranked(e, score);
        })
        .sorted(java.util.Comparator.comparing(Ranked::score).reversed())
        .toList();

    // ==== 4) Mapear a tu DTO de salida ====
    return ranked.stream()
        .map(r -> toResultadoBusqueda(r.e()))
        .toList();
}       

    // ───────── helpers genéricos
private <T> T getOrNull(java.util.function.Supplier<T> s) {
        try { return s.get(); } catch (Exception ex) { return null; }
    }
    
    private boolean estaCanceladoORechazado(Evento e) {
        if (e.getEventosEstado() == null) return false;
        return e.getEventosEstado().stream().anyMatch(ee -> {
            var nombre = ee.getEstadoEvento() != null ? ee.getEstadoEvento().getNombre() : null;
            return nombre != null && (
                nombre.equalsIgnoreCase("Cancelado") ||
                nombre.equalsIgnoreCase("Rechazado")
            );
        });
    }
    
    // arma el set de ids desde el filtro (si no existe en tu DTO, quedará vacío y no impacta)
    private java.util.Set<Long> getIdsDisciplinasFiltro(DTOBusquedaEventos filtro) {
        try {
                List<Long> ids = filtro.disciplinas();       // ajustá al nombre real de tu DTO
            return (ids == null) ? java.util.Set.of() : new java.util.HashSet<>(ids);
        } catch (Exception ex) {
            return java.util.Set.of();
        }
    }
    
  
    private List<Evento> aplicarFiltrosExcluyentes(List<Evento> base, DTOBusquedaEventos f, java.time.LocalDateTime ahora) {

        // 1) Texto (palabras >= 3 chars)
        String txt = safeLower(f.texto());
        List<String> terms = splitTerms(txt);

        // 2) Fechas
        final java.time.LocalDateTime desde = 
        f.fechaDesde() != null
                ? java.time.Instant.ofEpochMilli(f.fechaDesde())
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                : null;

        final java.time.LocalDateTime hasta = 
        f.fechaHasta() != null
                ? java.time.Instant.ofEpochMilli(f.fechaHasta())
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                : null;


        // 3) Horario
        Integer horaMin = f.horaDesde() != null ? f.horaDesde().intValue() : null;
        Integer horaMax = f.horaHasta() != null ? f.horaHasta().intValue() : null;

        // 4) Tipos de espacio (ids)
        java.util.List<Long> tiposEspacioIds = f.tiposEspacio();

        // 5) Disciplinas (ids)
        java.util.Set<Long> disciplinasFiltro = getIdsDisciplinasFiltro(f);

        // 6) Precio límite
        java.math.BigDecimal precioMax = f.precioLimite() != null ? new java.math.BigDecimal(f.precioLimite()) : null;

        // 7) SuperEventos flags
        Boolean incluirSE = f.buscarSupereventos();
        Boolean soloSE = !f.buscarEventos();

    
        return base.stream()
            // texto
            .filter(e -> terms.isEmpty() || matchTexto(e, terms))
            // fechas (si fijas)
            .filter(e -> {
                if (desde == null && hasta == null) return true;
                java.time.LocalDateTime ini = e.getFechaHoraInicio();
                java.time.LocalDateTime fin = e.getFechaHoraFin();
                if (ini == null || fin == null) return false;
                boolean okDesde = (desde == null) || (!ini.isBefore(desde));
                boolean okHasta = (hasta == null) || (!fin.isAfter(hasta));
                return okDesde && okHasta;
            })
            // horario
            .filter(e -> {
                if (horaMin == null || horaMax == null) return true;
                java.time.LocalTime ti = e.getFechaHoraInicio().toLocalTime();
                java.time.LocalTime tf = e.getFechaHoraFin().toLocalTime();
                int mi = ti.getHour()*60 + ti.getMinute();
                int mf = tf.getHour()*60 + tf.getMinute();
                if (horaMax > horaMin) { // mismo día
                    return (mi >= horaMin && mi <= horaMax) && (mf >= horaMin && mf <= horaMax);
                } else { // cruza medianoche
                    boolean iniOk = (mi >= horaMin) || (mi <= horaMax);
                    boolean finOk = (mf >= horaMin) || (mf <= horaMax);
                    return iniOk && finOk;
                }
            })
            // tipo de espacio
            .filter(e -> {
                if (tiposEspacioIds == null || tiposEspacioIds.isEmpty()) return true;
                var esp = e.getSubEspacio() != null ? e.getSubEspacio().getEspacio() : null;
                var tipoEspacio = (esp != null && esp.getTipoEspacio() != null) ? esp.getTipoEspacio().getId() : null;
                return tipoEspacio != null && tiposEspacioIds.contains(tipoEspacio);
             })
    
            // disciplinas (al menos una)
            .filter(e -> {
                if (disciplinasFiltro.isEmpty()) return true;
                if (e.getDisciplinasEvento() == null) return false;
                return e.getDisciplinasEvento().stream()
                        .anyMatch(de -> de.getDisciplina() != null && disciplinasFiltro.contains(de.getDisciplina().getId()));
            })
            // precio LIMITE (filtra si lo mandan)
            .filter(e -> {
                if (precioMax == null) return true;
                return precioTotal(e).compareTo(precioMax) <= 0;
            })
            // incluir/solo supereventos
            .filter(e -> {
                boolean esSE = (e.getSuperEvento() != null);
                if (Boolean.TRUE.equals(soloSE)) return esSE;
                if (Boolean.TRUE.equals(incluirSE)) return true; // mezcla
                // por defecto (ambos false o null): mostrar solo eventos "simples"
                return !esSE;
            })
            .toList();
    }
    
    // texto: nombre, descripcion, direccion, nombre del espacio, características, disciplinas
    private boolean matchTexto(Evento e, List<String> terms) {
        var sb = new StringBuilder();
        if (e.getNombre() != null) sb.append(' ').append(e.getNombre());
        if (e.getDescripcion() != null) sb.append(' ').append(e.getDescripcion());
    
        var esp = (e.getSubEspacio() != null) ? e.getSubEspacio().getEspacio() : null;
        if (esp != null) {
            if (esp.getNombre() != null) sb.append(' ').append(esp.getNombre());
            if (esp.getDescripcion() != null) sb.append(' ').append(esp.getDescripcion());
            if (esp.getDireccionUbicacion() != null) sb.append(' ').append(esp.getDireccionUbicacion());
            // características del espacio (nombres)
            // si las tenés en Espacio → características → nombre, concatenalas aquí
        }
        if (e.getDisciplinasEvento() != null) {
            e.getDisciplinasEvento().forEach(de -> {
                if (de.getDisciplina() != null && de.getDisciplina().getNombre() != null)
                    sb.append(' ').append(de.getDisciplina().getNombre());
            });
        }
        String haystack = sb.toString().toLowerCase();
        for (String t : terms) {
            if (t.length() > 2 && !haystack.contains(t)) return false;
        }
        return true;
    }
    
    private String safeLower(String s) { return s == null ? "" : s.toLowerCase(); }
    private java.util.Set<String> toSetLower(java.util.Collection<String> c) {
        if (c == null || c.isEmpty()) return java.util.Set.of();
        return c.stream().filter(java.util.Objects::nonNull).map(String::toLowerCase).collect(java.util.stream.Collectors.toSet());
    }
    private java.util.List<String> splitTerms(String s) {
        if (s == null) return java.util.List.of();
        return java.util.Arrays.stream(s.trim().toLowerCase().split("\\s+"))
            .filter(w -> !w.isBlank() && w.length() > 2)
            .toList();
    }



    // u: cercanía geo (metros)
    private double scoreU(Evento e, double centerLat, double centerLon, double maxdMeters) {
        var esp = e.getSubEspacio() != null ? e.getSubEspacio().getEspacio() : null;
        if (esp == null || esp.getLatitudUbicacion() == null || esp.getLongitudUbicacion() == null) return 0.0;
    
        double evLat = esp.getLatitudUbicacion().doubleValue();
        double evLon = esp.getLongitudUbicacion().doubleValue();
        double d = haversineMeters(centerLat, centerLon, evLat, evLon);
    
        double denom = Math.log(maxdMeters);
        if (denom <= 0) return 0.0;
    
        double num = Math.log(maxdMeters / Math.max(d, 1.0));
        double u = Math.max(num / denom, 0.0);
        return Double.isFinite(u) ? Math.max(0.0, Math.min(1.0, u)) : 0.0;
    }
    
    private static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000.0; // m
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*
                   Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
    
    // d: Jaccard (intersección / unión) entre disciplinas del filtro y del evento
    private double scoreD(Evento e, java.util.Set<Long> filtroIds) {
        if (filtroIds == null || filtroIds.isEmpty()) return 0.0;
        if (e.getDisciplinasEvento() == null || e.getDisciplinasEvento().isEmpty()) return 0.0;
    
        java.util.Set<Long> ev = e.getDisciplinasEvento().stream()
            .filter(de -> de.getDisciplina() != null)
            .map(de -> de.getDisciplina().getId())
            .collect(java.util.stream.Collectors.toSet());
    
        if (ev.isEmpty()) return 0.0;
    
        long inter = ev.stream().filter(filtroIds::contains).count();
        long union = java.util.stream.Stream.concat(ev.stream(), filtroIds.stream())
                     .collect(java.util.stream.Collectors.toSet()).size();
        if (union == 0) return 0.0;
        return (double) inter / (double) union;
    }
    
    // p: max((max_p - precioTotal) / max_p, 0)
    private double scoreP(java.math.BigDecimal precioTotal, java.math.BigDecimal maxP) {
        if (maxP == null || maxP.signum() <= 0) return 0.0;
        var diff = maxP.subtract(precioTotal);
        if (diff.signum() <= 0) return 0.0;
        var val = diff.divide(maxP, java.math.MathContext.DECIMAL64).doubleValue();
        return Math.max(0.0, Math.min(1.0, val));
    }
    
    // precio total (por ahora = precioInscripcion; si querés, sumamos comisión porcentual vigente)
    private java.math.BigDecimal precioTotal(Evento e) {
        return e.getPrecioInscripcion() == null ? java.math.BigDecimal.ZERO : e.getPrecioInscripcion();
    }
    
    // radio default (si no viene del filtro)
    private double paramMaxDistancia() {
        // podés leerlo de un parámetro global (ej. "max_d_m") si lo cargás en la tabla
        return 5000.0; // 5 km
    }

    private java.time.LocalDateTime toLdt(Long epochMs) {
        if (epochMs == null) return null;
        return java.time.Instant.ofEpochMilli(epochMs)
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }
   
    
    private DTOResultadoBusquedaEventos toResultadoBusqueda(Evento e) {
        boolean esSE = (e.getSuperEvento() != null);
    
        Long inicioEpoch = (e.getFechaHoraInicio() != null)
            ? e.getFechaHoraInicio().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
            : null;
    
        // precio (double) “más comisión y adicional” → hoy usamos precioInscripcion directo
        Double precio = (e.getPrecioInscripcion() != null) ? e.getPrecioInscripcion().doubleValue() : 0d;
    
        String nombreEspacio = null;
        var esp = e.getSubEspacio() != null ? e.getSubEspacio().getEspacio() : null;
        if (esp != null) {
            nombreEspacio = (esp.getNombre() != null) ? esp.getNombre() : esp.getDireccionUbicacion();
        }
    
        java.util.List<String> disciplinas = java.util.Collections.emptyList();
        if (e.getDisciplinasEvento() != null) {
            disciplinas = e.getDisciplinasEvento().stream()
                    .map(de -> de.getDisciplina() != null ? de.getDisciplina().getNombre() : null)
                    .filter(java.util.Objects::nonNull)
                    .sorted(String::compareToIgnoreCase)
                    .toList();
        }
    
        // próximo evento si es superevento (si querés, calculá con repo; acá null por simplicidad)
        Long proximo = null;
    
        return new DTOResultadoBusquedaEventos(
            esSE,
            e.getId(),
            e.getNombre(),
            inicioEpoch,
            precio,
            nombreEspacio,
            disciplinas,
            proximo
        );
    }
    


    @Override
    @Transactional
    public List<DTOResultadoBusquedaMisEventos> buscarMisEventos(DTOBusquedaMisEventos filtro, String username) {
        return eventoRepo.findAll(EventoSpecs.byFiltroMisEventos(filtro, username),
                        Sort.by("fechaHoraInicio").descending())
                .stream()
                .map(e -> EventoSearchMapper.toResultadoBusquedaMis(e, username))
                .toList();
    }
    

    @Override
    @Transactional
    public DTOEventoDetalle obtenerEventoDetalle(long idEvento) {
        Evento e = eventoRepo.findByIdForDetalle(idEvento)
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));
    
        // ✅ Cargar inscripciones activas
        List<Inscripcion> inscripcionesActivas = inscripcionRepo.findActivasByEventoId(e.getId());
        e.setInscripciones(inscripcionesActivas);
    
        // Inicializar colecciones LAZY necesarias (disciplinas)
        if (e.getDisciplinasEvento() != null) {
            e.getDisciplinasEvento().forEach(de -> {
                if (de.getDisciplina() != null) de.getDisciplina().getNombre();
            });
        }
    
        // ===============================
        // 1️⃣ Obtener usuario autenticado
        // ===============================
        String username = null;
        try {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception ignored) {}
        
        // 🔒 Creamos una variable final para usar en los streams
        final String currentUser = username;
        // ===============================
        // 2️⃣ Determinar estado del evento
        // ===============================
        boolean cancelado = e.getEventosEstado() != null &&
                e.getEventosEstado().stream()
                        .anyMatch(ee -> ee.getEstadoEvento() != null &&
                                "Cancelado".equalsIgnoreCase(ee.getEstadoEvento().getNombre()));
    
        String motivoCancelacion = null;
        if (cancelado && e.getEventosEstado() != null) {
            var ultimoCancelado = e.getEventosEstado().stream()
                    .filter(ee -> ee.getEstadoEvento() != null &&
                            "Cancelado".equalsIgnoreCase(ee.getEstadoEvento().getNombre()))
                    .max(java.util.Comparator.comparing(ee -> ee.getFechaHoraAlta()))
                    .orElse(null);
            if (ultimoCancelado != null && ultimoCancelado.getDescripcion() != null) {
                motivoCancelacion = ultimoCancelado.getDescripcion();
            }
        }
    
        // ===============================
        // 3️⃣ Cupo y participación
        // ===============================
        int participantes = inscripcionRepo.countParticipantesEfectivos(e.getId());
        boolean cupoLleno = e.getCantidadMaximaParticipantes() != null &&
                participantes >= e.getCantidadMaximaParticipantes();
    
        // ===============================
        // 4️⃣ Determinar rol del usuario
        // ===============================
        String rol = "ninguno";
        if (currentUser != null) {
        
            // 🟣 Organizador / Administrador
            if (e.getAdministradoresEvento() != null) {
                var admin = e.getAdministradoresEvento().stream()
                        .filter(a -> a.getUsuario() != null && a.getUsuario().getUsername().equals(currentUser))
                        .filter(a -> a.getFechaHoraBaja() == null)
                        .findFirst()
                        .orElse(null);
        
                if (admin != null && admin.getTipoAdministradorEvento() != null) {
                    String tipo = admin.getTipoAdministradorEvento().getNombre();
                    if ("Organizador".equalsIgnoreCase(tipo)) rol = "organizador";
                    else if ("Administrador".equalsIgnoreCase(tipo)) rol = "administrador";
                }
            }
    
            // 🔹 Participante
            if ("ninguno".equals(rol)) {
                boolean inscripto = inscripcionRepo.countActivasByEventoIdAndUsuarioUsername(e.getId(), username) > 0;
                if (inscripto) rol = "participante";
            }
    
            // 🔹 Encargado de Subespacio
            if ("ninguno".equals(rol) && e.getSubEspacio() != null) {
                var encargado = e.getSubEspacio().getEncargadoSubEspacio();
                if (encargado != null &&
                    encargado.getUsuario() != null &&
                    encargado.getUsuario().getUsername().equals(username) &&
                    encargado.getFechaHoraBaja() == null) {
                    rol = "encargado";
                }
            }
        }
    
        // ===============================
        // 5️⃣ Permisos según rol
        // ===============================
        boolean puedeDenunciar = "participante".equals(rol);
        boolean puedeCancelarInscripcion = "participante".equals(rol)
                && e.getFechaHoraInicio().isAfter(LocalDateTime.now());
        boolean puedeAdministrar = "organizador".equals(rol) || "administrador".equals(rol);
        boolean puedeChatear = !"ninguno".equals(rol);
        boolean puedeCompartir = "participante".equals(rol);
    
        // ===============================
        // 6️⃣ Calcular precios
        // ===============================
        double precioBase = e.getPrecioInscripcion() != null ? e.getPrecioInscripcion().doubleValue() : 0d;
        double precioTotal = calcularPrecioTotal(e);
    
        // ===============================
        // 7️⃣ Construir DTOs anidados
        // ===============================
        DTOEventoDetalle.Espacio espacio = null;
        DTOEventoDetalle.Subespacio subespacio = null;
    
        if (e.getSubEspacio() != null && e.getSubEspacio().getEspacio() != null) {
            var esp = e.getSubEspacio().getEspacio();
    
            Double lat = (esp.getLatitudUbicacion() != null) ? esp.getLatitudUbicacion().doubleValue() : null;
            Double lon = (esp.getLongitudUbicacion() != null) ? esp.getLongitudUbicacion().doubleValue() : null;
    
            espacio = new DTOEventoDetalle.Espacio(
                    esp.getId(),
                    esp.getNombre(),
                    esp.getDireccionUbicacion(),
                    lat,
                    lon
            );
    
            subespacio = new DTOEventoDetalle.Subespacio(
                    e.getSubEspacio().getId(),
                    e.getSubEspacio().getNombre(),
                    e.getSubEspacio().getDescripcion()
            );
        }
    
        List<String> disciplinas = (e.getDisciplinasEvento() == null)
                ? List.of()
                : e.getDisciplinasEvento().stream()
                    .filter(de -> de.getDisciplina() != null)
                    .map(de -> de.getDisciplina().getNombre())
                    .filter(Objects::nonNull)
                    .toList();
    
        List<DTOEventoDetalle.Inscripto> inscriptos = (e.getInscripciones() == null)
                ? List.of()
                : e.getInscripciones().stream()
                    .filter(i -> i.getUsuario() != null)
                    .map(i -> new DTOEventoDetalle.Inscripto(
                            i.getUsuario().getUsername(),
                            i.getUsuario().getNombre(),
                            i.getUsuario().getApellido(),
                            i.getUsuario().getFotoPerfil() // si existe este campo
                    ))
                    .toList();
    
        // ===============================
        // 8️⃣ Construcción final
        // ===============================
        return new DTOEventoDetalle(
                e.getId(),
                e.getNombre(),
                e.getDescripcion(),
                TimeUtil.toMillis(e.getFechaHoraInicio()),
                TimeUtil.toMillis(e.getFechaHoraFin()),
                precioBase,
                precioTotal,
                disciplinas,
                espacio,
                subespacio,
                cancelado,
                motivoCancelacion,
                cupoLleno,
                rol,
                puedeDenunciar,
                puedeCancelarInscripcion,
                puedeAdministrar,
                puedeChatear,
                puedeCompartir,
                inscriptos
        );
    }
    
    
    private double calcularPrecioTotal(Evento e) {
        if (e.getPrecioInscripcion() == null)
            return 0d;
    
        double base = e.getPrecioInscripcion().doubleValue();
    
        // 💰 Comisión Evtnet (20%)
        double comisionEvtnet = base * 0.20;
    
        // 💡 Adicional (10%) si el espacio es de tipo "Privado"
        double adicional = 0d;
        if (e.getSubEspacio() != null &&
            e.getSubEspacio().getEspacio() != null &&
            e.getSubEspacio().getEspacio().getTipoEspacio() != null &&
            "Privado".equalsIgnoreCase(e.getSubEspacio().getEspacio().getTipoEspacio().getNombre())) {
    
            adicional = base * 0.10;
        }
    
        return base + comisionEvtnet + adicional;
    }
    
    
    @Override
    @Transactional
    public DTODatosCreacionEvento obtenerDatosCreacionEvento(Long idSubespacio) {
        if (idSubespacio == null)
            throw new HttpErrorException(400, "Debe indicar un subespacio para crear el evento");
    
        SubEspacio subespacio = subEspacioRepo.findById(idSubespacio)
                .orElseThrow(() -> new HttpErrorException(404, "Subespacio no encontrado"));
    
        Espacio espacio = subespacio.getEspacio();
    
        // 1️⃣ Datos básicos
        boolean espacioPublico = espacio.getTipoEspacio() != null &&
                "Público".equalsIgnoreCase(espacio.getTipoEspacio().getNombre());
    
        boolean requiereAprobarEventos = Boolean.TRUE.equals(espacio.getRequiereAprobarEventos());
    
        int capacidadMaxima = subespacio.getCapacidadmaxima();
    
        // 2️⃣ Usuario autenticado
        String username = null;
        try {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception ignored) {}
    
        // 🔒 Creamos una variable final para usar en los streams
        final String currentUser = username;

        boolean esAdministradorEspacio = false;
        boolean puedeElegirHorarioLibre = false;
    
        if (currentUser != null && espacio.getAdministradoresEspacio() != null) {
                var admin = espacio.getAdministradoresEspacio().stream()
                        .filter(a -> a.getUsuario() != null &&
                                a.getUsuario().getUsername().equals(currentUser) &&
                                a.getFechaHoraBaja() == null)
                        .findFirst()
                        .orElse(null);
            
                if (admin != null && admin.getTipoAdministradorEspacio() != null) {
                    String tipo = admin.getTipoAdministradorEspacio().getNombre();
                    esAdministradorEspacio = "Administrador de Espacio".equalsIgnoreCase(tipo)
                            || "Propietario".equalsIgnoreCase(tipo);
            
                    // Solo los administradores o propietarios pueden elegir horarios libres
                    puedeElegirHorarioLibre = esAdministradorEspacio && !espacioPublico;
                }
        }
    
        // 3️⃣ Disciplinas soportadas
        List<String> disciplinasSoportadas = subespacio.getDisciplinasSubespacio() != null
                ? subespacio.getDisciplinasSubespacio().stream()
                    .filter(ds -> ds.getDisciplina() != null)
                    .map(ds -> ds.getDisciplina().getNombre())
                    .toList()
                : List.of();
    
        // 4️⃣ Cronogramas (solo si el usuario no puede elegir horario libre)
        List<DTODatosCreacionEvento.Cronograma> cronogramas = List.of();
    
        if (!puedeElegirHorarioLibre && subespacio.getConfiguracionesHorarioEspacio() != null) {
            cronogramas = subespacio.getConfiguracionesHorarioEspacio().stream()
                    .map(config -> new DTODatosCreacionEvento.Cronograma(
                            config.getId(),
                            config.getFechaDesde(),
                            config.getFechaHasta(),
                            config.getDiasAntelacion(),
                            config.getHorariosEspacio() != null
                                    ? config.getHorariosEspacio().stream()
                                        .map(h -> new DTODatosCreacionEvento.Horario(
                                                h.getDiaSemana(),
                                                h.getHoraDesde(),
                                                h.getHoraHasta(),
                                                h.getPrecioOrganizacion() != null
                                                        ? h.getPrecioOrganizacion().toPlainString()
                                                        : "0.00",
                                                h.getAdicionalPorInscripcion()
                                        ))
                                        .toList()
                                    : List.of()
                    ))
                    .toList();
        }
    
        // 5️⃣ Parámetros del sistema (por ahora fijo)
        double comision = 0.12;
        /*
        // FUTURO: obtener desde ParametroSistema
        double comision = parametroSistemaRepo.findByClave("COMISION_INSCRIPCION_EVENTO")
            .map(p -> Double.parseDouble(p.getValor()))
            .orElse(0.12);
        */
    
        int diasHaciaAdelante = 30;
    
        // 6️⃣ Retornar DTO completo
        return new DTODatosCreacionEvento(
                espacio.getNombre(),
                subespacio.getNombre(),
                comision,
                espacioPublico,
                requiereAprobarEventos,
                esAdministradorEspacio,
                puedeElegirHorarioLibre,
                diasHaciaAdelante,
                capacidadMaxima,
                disciplinasSoportadas,
                cronogramas
        );
    }
    
    

    @Override
    @Transactional
    public long crearEvento(DTOEventoCreate r) throws Exception {
    
        // 1️⃣ Validaciones básicas
        if (r.getFechaHoraInicio() == null || r.getFechaHoraFin() == null)
            throw new HttpErrorException(400, "Fecha/hora de inicio y fin son requeridas");
    
        if (r.getSubEspacioId() == null)
            throw new HttpErrorException(400, "El subEspacioId es requerido");
    
        if (r.getFechaHoraFin().isBefore(r.getFechaHoraInicio()))
            throw new HttpErrorException(400, "La fecha/hora de fin no puede ser anterior al inicio");
    
        // 2️⃣ Buscar subespacio y validar capacidad
        SubEspacio subEspacio = subEspacioRepo.findById(r.getSubEspacioId())
                .orElseThrow(() -> new HttpErrorException(404, "Subespacio no encontrado"));
    
        Integer capacidad = subEspacio.getCapacidadmaxima();
        if (capacidad != null) {
            if (r.getCantidadMaximaParticipantes() != null && r.getCantidadMaximaParticipantes() > capacidad)
                throw new HttpErrorException(400, "La cantidad máxima de participantes supera la capacidad del subespacio");
            if (r.getCantidadMaximaInvitados() != null && r.getCantidadMaximaInvitados() > capacidad)
                throw new HttpErrorException(400, "La cantidad máxima de invitados supera la capacidad del subespacio");
        }
    
        // 3️⃣ Usuario creador
        final String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));
    
        Espacio espacio = subEspacio.getEspacio();
    
        // 4️⃣ Determinar si el usuario es administrador del espacio (para permitir horario libre)
        boolean esAdministradorEspacio = false;
        boolean puedeElegirHorarioLibre = false;
    
        if (espacio.getAdministradoresEspacio() != null) {
            var admin = espacio.getAdministradoresEspacio().stream()
                    .filter(a -> a.getUsuario() != null &&
                            a.getUsuario().getUsername().equals(username) &&
                            a.getFechaHoraBaja() == null)
                    .findFirst()
                    .orElse(null);
    
            if (admin != null && admin.getTipoAdministradorEspacio() != null) {
                String tipo = admin.getTipoAdministradorEspacio().getNombre();
                esAdministradorEspacio = "Administrador de Espacio".equalsIgnoreCase(tipo)
                        || "Propietario".equalsIgnoreCase(tipo);
    
                puedeElegirHorarioLibre = esAdministradorEspacio &&
                        !"Público".equalsIgnoreCase(espacio.getTipoEspacio().getNombre());
            }
        }
    
        // 5️⃣ Validar horario (si no puede elegir libremente, debe estar dentro de un cronograma del subespacio)
        if (!puedeElegirHorarioLibre) {
            boolean dentroDeCronograma = subEspacio.getConfiguracionesHorarioEspacio() != null &&
                    subEspacio.getConfiguracionesHorarioEspacio().stream().anyMatch(cfg ->
                            !r.getFechaHoraInicio().isBefore(cfg.getFechaDesde()) &&
                            !r.getFechaHoraFin().isAfter(cfg.getFechaHasta())
                    );
    
            if (!dentroDeCronograma)
                throw new HttpErrorException(400, "El evento no coincide con un rango de cronograma disponible del subespacio");
        }
    
        // 6️⃣ Validar disciplinas permitidas por el subespacio
        List<DisciplinaEvento> hijos = new ArrayList<>();
        if (r.getDisciplinasEvento() != null && !r.getDisciplinasEvento().isEmpty()) {
            for (DTODisciplinaEventoCreate deDto : r.getDisciplinasEvento()) {
                Long idDisciplina = (deDto.getDisciplina() != null) ? deDto.getDisciplina().getId() : null;
                if (idDisciplina == null)
                    throw new HttpErrorException(400, "disciplina.id es requerido en cada disciplinaEvento");
    
                boolean soportada = disciplinaSubEspacioRepo.existsBySubEspacioIdAndDisciplinaId(subEspacio.getId(), idDisciplina);
                if (!soportada)
                    throw new HttpErrorException(400, "La disciplina id=" + idDisciplina + " no está habilitada en este subespacio");
    
                Disciplina d = disciplinaBaseRepo.findById(idDisciplina)
                        .orElseThrow(() -> new HttpErrorException(404, "Disciplina no encontrada: id=" + idDisciplina));
    
                hijos.add(DisciplinaEvento.builder().evento(null).disciplina(d).build());
            }
        }
    
        // 7️⃣ Crear el evento
        Evento e = new Evento();
        e.setNombre(r.getNombre());
        e.setDescripcion(r.getDescripcion());
        e.setFechaHoraInicio(r.getFechaHoraInicio());
        e.setFechaHoraFin(r.getFechaHoraFin());
        e.setPrecioInscripcion(r.getPrecioInscripcion());
        e.setCantidadMaximaInvitados(r.getCantidadMaximaInvitados());
        e.setCantidadMaximaParticipantes(r.getCantidadMaximaParticipantes());
        e.setPrecioOrganizacion(r.getPrecioOrganizacion());
        e.setSubEspacio(subEspacio);
        e.setDisciplinasEvento(hijos);
    
        // 8️⃣ Asignar usuario como organizador y administrador
        TipoAdministradorEvento tipoOrg = tipoAdminEventoRepo.findByNombreIgnoreCase("Organizador")
                .orElseThrow(() -> new HttpErrorException(500, "TipoAdministradorEvento 'Organizador' no encontrado"));
        TipoAdministradorEvento tipoAdm = tipoAdminEventoRepo.findByNombreIgnoreCase("Administrador")
                .orElseThrow(() -> new HttpErrorException(500, "TipoAdministradorEvento 'Administrador' no encontrado"));
    
        List<AdministradorEvento> admins = List.of(
                AdministradorEvento.builder().evento(e).usuario(usuario).tipoAdministradorEvento(tipoOrg)
                        .fechaHoraAlta(LocalDateTime.now()).build(),
                AdministradorEvento.builder().evento(e).usuario(usuario).tipoAdministradorEvento(tipoAdm)
                        .fechaHoraAlta(LocalDateTime.now()).build()
        );
        e.setAdministradoresEvento(admins);
    
        // 9️⃣ Crear Chat asociado
        Chat chat = Chat.builder()
                .tipo(Chat.Tipo.EVENTO)
                .fechaHoraAlta(LocalDateTime.now())
                .evento(e)
                .build();
        e.setChat(chat);
    
        // 🔟 Guardar evento
        Evento saved = eventoRepo.save(e);
    
        // 🔟➕ Crear estado "En Revisión" si el espacio lo requiere
        boolean requiereAprobacion = espacio != null && Boolean.TRUE.equals(espacio.getRequiereAprobarEventos());
        if (requiereAprobacion) {
            EstadoEvento estadoEnRevision = estadoEventoRepo.findByNombreIgnoreCase("En Revisión")
                    .orElseThrow(() -> new HttpErrorException(500, "EstadoEvento 'En Revisión' no encontrado"));
    
            EventoEstado ee = new EventoEstado();
            ee.setEvento(saved);
            ee.setEstadoEvento(estadoEnRevision);
            ee.setFechaHoraAlta(LocalDateTime.now());
            eventoEstadoRepo.save(ee);
        }
    
        return saved.getId();
    }
    
    


    @Override
    @Transactional
    public int obtenerCantidadEventosSuperpuestos(long idEspacio, long fechaDesdeMillis, long fechaHastaMillis) {
        LocalDateTime desde = LocalDateTime.ofEpochSecond(fechaDesdeMillis / 1000, 0, java.time.ZoneOffset.UTC);
        LocalDateTime hasta = LocalDateTime.ofEpochSecond(fechaHastaMillis / 1000, 0, java.time.ZoneOffset.UTC);
        return eventoRepo.contarSuperpuestosPorEspacio(idEspacio, desde, hasta);
    }

    @Override @Transactional
    public DTOEventoParaInscripcion obtenerEventoParaInscripcion(long idEvento) {
        // 🔁 cambia a findByIdForDetalle
        Evento e = eventoRepo.findByIdForDetalle(idEvento)
            .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        DTOEventoParaInscripcion.Espacio espacio = null;

		espacio = DTOEventoParaInscripcion.Espacio.builder()
			.id(e.getSubEspacio().getEspacio().getId())
			.nombre(e.getSubEspacio().getEspacio().getNombre())
			.descripcion(e.getSubEspacio().getEspacio().getDescripcion())
			.build();

        return DTOEventoParaInscripcion.builder()
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .idSuperevento(e.getSuperEvento() != null ? e.getSuperEvento().getId() : null)
                .fechaDesde(e.getFechaHoraInicio())
                .fechaHasta(e.getFechaHoraFin())
                .espacio(espacio)
                .direccion(e.getSubEspacio().getEspacio().getDireccionUbicacion())
                .ubicacion(new DTOEventoParaInscripcion.Ubicacion(
                        e.getSubEspacio().getEspacio().getLatitudUbicacion().doubleValue(),
                        e.getSubEspacio().getEspacio().getLongitudUbicacion().doubleValue()))
                .precioPorAsistente(e.getPrecioInscripcion())
                .cantidadMaximaInvitados(e.getCantidadMaximaInvitados())
                .limiteParticipantes(e.getCantidadMaximaParticipantes())
                .build();
    }

	@Override
	@Transactional
	public DTOVerificacionPrePago verificarDatosPrePago(DTOInscripcion dto) throws Exception {
		boolean valido = verificarDatosPrePagoBool(dto);

		ArrayList<DTOPreferenciaPago> prefs = new ArrayList<>();

		if (valido) {
			//TO-DO
			//Generar preferencias para pagar
			//Está hardcodeado para no tener problemas mientras cambian las entidades
			//Hay que revisar TODO ESTO
			Evento e = eventoRepo.findById(dto.getIdEvento())
				.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

			BigDecimal comision = new BigDecimal(0.1);

			String url = "/Evento/" + e.getId() + "/Inscribirme";

			Usuario organizador = e.getOrganizador();

			prefs.add(mercadoPagoSingleton.createPreference("Inscripción a evento " + e.getNombre(), e.getPrecioInscripcion(), comision, organizador, url));

            Usuario propietario = e.getOrganizador();

			prefs.add(mercadoPagoSingleton.createPreference("Adicional a espacio por inscripción a evento " + e.getNombre(), e.getPrecioInscripcion(), comision, propietario, url));
		}

		return DTOVerificacionPrePago.builder()
			.valido(valido)
			.preferencias(prefs)
			.build();
	}

	private boolean verificarDatosPrePagoBool(DTOInscripcion dto) {
		Evento e = eventoRepo.findById(dto.getIdEvento())
				.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));
		
		String username = CurrentUser.getUsername().orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

		// ya inscripto (solo si tiene una inscripción activa)
		if (inscripcionRepo.countActivasByEventoIdAndUsuarioUsername(e.getId(), username) > 0) 
		return false;

		// Zona configurada en application.properties
		ZoneId zone = ZoneId.of(appTimezone);

		// ahora en zona ARG
		ZonedDateTime ahora = ZonedDateTime.now(zone);

		// inicio interpretado directamente en la zona configurada (sin forzar UTC)
		ZonedDateTime inicio = e.getFechaHoraInicio().atZone(zone);

		if (ahora.isAfter(inicio)) {
				return false; // bloquear solo si ya pasó
		}

		// capacidad de participantes
		int actuales = inscripcionRepo.countParticipantesEfectivos(e.getId());
		int nuevos = 1 + (dto.getInvitados() != null ? dto.getInvitados().size() : 0);
		Integer limite = e.getCantidadMaximaParticipantes();
		if (limite != null && actuales + nuevos > limite) return false;

		// límite de invitados por inscripción
		if (e.getCantidadMaximaInvitados() != null && dto.getInvitados() != null &&
				dto.getInvitados().size() > e.getCantidadMaximaInvitados()) return false;

		// precio mínimo
		if (dto.getPrecioInscripcion() != null && e.getPrecioInscripcion() != null &&
				dto.getPrecioInscripcion().compareTo(e.getPrecioInscripcion()) < 0) return false;

		return true;
	}



        @Override 
        @Transactional
        public void inscribirse(DTOInscripcion dto) throws Exception {
        if (!verificarDatosPrePagoBool(dto))
                throw new HttpErrorException(400, "Datos inválidos para inscribirse");

        Evento e = eventoRepo.findById(dto.getIdEvento())
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));
		
		String username = CurrentUser.getUsername()
			.orElseThrow(() -> new HttpErrorException(404, "Debe iniciar sesión antes de intentar inscribirse"));
		//En este método, el que se inscribe el que lo llama. Username ESTABA en el DTO para cuando un admin lo inscribía
        //String username = resolveUsername(dto.getUsername()); // ← token si falta
        Usuario u = usuarioRepo.findByUsername(username)
            .orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

        mercadoPagoSingleton.verifyPayments(dto.getDatosPago());

        Inscripcion ins = new Inscripcion();
        ins.setEvento(e);
        ins.setUsuario(u);
        // 🔹 Corregido: guarda con hora de Argentina
        ins.setFechaHoraAlta(LocalDateTime.now(ZONA_ARG));

        // precio: usa el del DTO si viene, sino el del evento
        ins.setPrecioInscripcion(dto.getPrecioInscripcion() != null
                ? dto.getPrecioInscripcion()
                : e.getPrecioInscripcion());

        // ¡crítico!
        ins.setPermitirDevolucionCompleta(Boolean.FALSE);

        inscripcionRepo.save(ins);

        if (dto.getInvitados() != null) {
                for (DTOInscripcion.Invitado i : dto.getInvitados()) {
                Invitado inv = new Invitado();
                inv.setInscripcion(ins);
                inv.setNombre(i.getNombre());
                inv.setApellido(i.getApellido());
                inv.setDni(i.getDni());
                invitadoRepo.save(inv);
                }
        }
        }



        @Override
        @Transactional
        public void desinscribirse(long idEvento) throws Exception {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
        
            Inscripcion ins = inscripcionRepo.findActivaByEventoIdAndUsuarioUsername(idEvento, username)
            .orElseThrow(() -> new HttpErrorException(404, "No existe inscripción activa"));
            
			//TO-DO: colocar el payment id, extraído del comprobante
			String paymentId = "abcd";
			mercadoPagoSingleton.refundPayment(paymentId);

            // Eliminar invitados asociados
            invitadoRepo.deleteByInscripcionId(ins.getId());
        
            // En vez de eliminar la inscripción, la marcamos con fecha de baja
            ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");
            ins.setFechaHoraBaja(LocalDateTime.now(zone));
            
            inscripcionRepo.save(ins);
        }
        

    @Override
    @Transactional
    public Number obtenerMontoDevolucionCancelacion(long idEvento, String username) {
        // 🔁 cambia a findByIdForDetalle
        Evento e = eventoRepo.findByIdForDetalle(idEvento)
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        BigDecimal totalPagado = comprobanteRepo
                .totalPagadoPorEventoYUsuario(idEvento, username)
                .orElse(BigDecimal.ZERO);

        if (totalPagado.signum() <= 0) return BigDecimal.ZERO;

        long minutosHastaInicio = Math.max(
                0L,
                java.time.Duration.between(LocalDateTime.now(), e.getFechaHoraInicio()).toMinutes()
        );

        BigDecimal factor = BigDecimal.ZERO;

        if (e.getPorcentajesReintegroCancelacion() != null && !e.getPorcentajesReintegroCancelacion().isEmpty()) {
            Optional<PorcentajeReintegroCancelacionInscripcion> mejor = e.getPorcentajesReintegroCancelacion().stream()
                    .filter(p -> p.getMinutosLimite() != null && minutosHastaInicio >= p.getMinutosLimite())
                    .max(Comparator.comparing(PorcentajeReintegroCancelacionInscripcion::getMinutosLimite));

            if (mejor.isPresent() && mejor.get().getPorcentaje() != null) {
                factor = mejor.get().getPorcentaje().divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            }
        }

        return totalPagado.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

        @Override
        @Transactional
        public DTOModificarEvento obtenerDatosModificacionEvento(long idEvento) throws Exception {
        // 🔁 cambia a findByIdForDetalle
        Evento e = eventoRepo.findByIdForDetalle(idEvento)
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        int participantes = inscripcionRepo.countParticipantesEfectivos(e.getId());
        int maxInvPorInscripcion = inscripcionRepo.maxInvitadosPorInscripcionVigente(e.getId());

        List<DTOModificarEvento.ItemIdNombre> disciplinas = new ArrayList<>();
        if (e.getDisciplinasEvento() != null) {
                for (DisciplinaEvento de : e.getDisciplinasEvento()) {
                disciplinas.add(new DTOModificarEvento.ItemIdNombre(
                        de.getDisciplina().getId(), de.getDisciplina().getNombre()));
                }
        }

        List<DTOModificarEvento.RangoReintegro> rangos = new ArrayList<>();
        if (e.getPorcentajesReintegroCancelacion() != null) {
                e.getPorcentajesReintegroCancelacion().stream()
                        .sorted(Comparator.comparing(PorcentajeReintegroCancelacionInscripcion::getMinutosLimite))
                        .forEach(p -> {
                        int[] dhm = splitMinutes(p.getMinutosLimite());
                        rangos.add(DTOModificarEvento.RangoReintegro.builder()
                                .dias(dhm[0])
                                .horas(dhm[1])
                                .minutos(dhm[2])
                                .porcentaje(
                                        p.getPorcentaje() != null
                                                ? p.getPorcentaje().setScale(0, RoundingMode.HALF_UP).intValue()
                                                : 0
                                )
                                .build());
                        });
        }

        // 🔹 Validar si el usuario actual es administrador del evento
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean esAdministrador = eventoRepo.existsByEventoIdAndAdministradorUsername(idEvento, username);

        // 🔹 Validar si el usuario actual es organizador del evento
        boolean esOrganizador = e.getOrganizador() != null && e.getOrganizador().getUsername().equals(username);

        return DTOModificarEvento.builder()
                .id(e.getId())
                .nombre(e.getNombre() != null ? e.getNombre() : "")
                .descripcion(e.getDescripcion() != null ? e.getDescripcion() : "")
                .idEspacio(e.getSubEspacio().getEspacio().getId())          // ⚡ nunca null
                .nombreEspacio(e.getSubEspacio().getEspacio().getNombre()) // ⚡ nunca null
                .usarCronograma(false)
                .fechaDesde(e.getFechaHoraInicio())
                .fechaHasta(e.getFechaHoraFin())
                .horarioId(null)         // ⚡ nunca null
                .precioOrganizacion(e.getPrecioOrganizacion() != null ? e.getPrecioOrganizacion() : BigDecimal.ZERO)
                .direccion(e.getSubEspacio().getEspacio().getDireccionUbicacion())
                .ubicacion(new DTOModificarEvento.Ubicacion(
                    e.getSubEspacio().getEspacio().getLatitudUbicacion().doubleValue(),
                    e.getSubEspacio().getEspacio().getLongitudUbicacion().doubleValue()))
                .disciplinas(disciplinas != null ? disciplinas : List.of()) // ⚡ lista vacía si no hay
                .precioInscripcion(e.getPrecioInscripcion() != null ? e.getPrecioInscripcion() : BigDecimal.ZERO)
                .comisionInscripcion(BigDecimal.valueOf(0.12))
                .cantidadMaximaParticipantes(e.getCantidadMaximaParticipantes() != null ? e.getCantidadMaximaParticipantes() : 0)
                .cantidadMaximaInvitados(e.getCantidadMaximaInvitados() != null ? e.getCantidadMaximaInvitados() : 0)
                .cantidadParticipantesActual(participantes)
                .cantidadMaximaInvitadosPorInvitacionEfectiva(maxInvPorInscripcion)
                .crearSuperevento(false) // ⚡ siempre boolean
                .superevento(e.getSuperEvento() != null
                        ? new DTOModificarEvento.Superevento(
                                e.getSuperEvento().getId(),
                                e.getSuperEvento().getNombre() != null ? e.getSuperEvento().getNombre() : "",
                                e.getSuperEvento().getDescripcion() != null ? e.getSuperEvento().getDescripcion() : "")
                        : new DTOModificarEvento.Superevento(0L, "", "")) // ⚡ objeto vacío, nunca null
                .rangosReintegro(rangos != null ? rangos : List.of()) // ⚡ lista vacía
                .espacioPublico(null)
                .administradorEspacio(false) // ⚡ default
                .administradorEvento(esAdministrador)   // ⚡ true/false
                .organizadorEvento(esOrganizador)       // ⚡ true/false
                .diasHaciaAdelante(30)
                .build();


        }


        @Override
        @Transactional
        public void modificarEvento(DTOModificarEvento dto) {
        Evento e = eventoRepo.findById(dto.getId())
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        // Fechas (ya llegan como LocalDateTime gracias al deserializador)
        if (dto.getFechaDesde() == null || dto.getFechaHasta() == null) {
                throw new HttpErrorException(400, "Fechas requeridas");
        }

        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        e.setFechaHoraInicio(dto.getFechaDesde());
        e.setFechaHoraFin(dto.getFechaHasta());

		/* 
		 * TO-DO: El precio de organización cambia según el horario del cronograma elegido,
		 * y solo cuando lo hace un usuario no admin del espacio. Si no, es 0.
		 * El precio de organización se debería setear en base al horario correspondiente,
		 * y si ninguno coincide, tirar una excepción.
		 * 
		 * Siempre validar las cosas que dice la US, como de participantes >= 2, aunque lo haga el front también
		 * 
		 * La US también aclaraba que, cuando cambian los precios de inscripción u organización, 
		 * puede requerirse un pago adicional o una devolución.
		 * Dejemos lo del pago adicional para después, necesitamos modificar bastante el front y el back para eso.
		 */

        // Precios y cantidades
        e.setPrecioInscripcion(dto.getPrecioInscripcion());
        e.setPrecioOrganizacion(dto.getPrecioOrganizacion());
        e.setCantidadMaximaInvitados(dto.getCantidadMaximaInvitados());
        e.setCantidadMaximaParticipantes(dto.getCantidadMaximaParticipantes());

        // Disciplinas
        if (dto.getDisciplinas() != null) {
			if (e.getDisciplinasEvento() == null) e.setDisciplinasEvento(new ArrayList<>());
			e.getDisciplinasEvento().clear();
			for (DTOModificarEvento.ItemIdNombre it : dto.getDisciplinas()) {
			Disciplina d = disciplinaBaseRepo.findById(it.getId())
					.orElseThrow(() -> new HttpErrorException(400, "Disciplina no encontrada"));
			e.getDisciplinasEvento().add(DisciplinaEvento.builder()
					.evento(e).disciplina(d).build());
			}
        }


        // Superevento
        if (dto.getSuperevento() != null && dto.getSuperevento().getId() != null && dto.getSuperevento().getId() > 0) {
                e.setSuperEvento(superEventoRepo.findById(dto.getSuperevento().getId())
                        .orElseThrow(() -> new HttpErrorException(404, "Superevento no encontrado")));
        } else {
                e.setSuperEvento(null);
        }

        eventoRepo.save(e);
    }


	/*
	 * TO-DO: No hay endpoint de esta función, no está en el controller
	 */
    @Override
    @Transactional
    public DTOInscripcionesEvento obtenerInscripciones(long idEvento, String texto) throws Exception {
        Evento e = eventoRepo.findById(idEvento)
            .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean esAdministrador = eventoRepo.existsByEventoIdAndAdministradorUsername(idEvento, currentUser);
        boolean esOrganizador = e.getOrganizador() != null &&
            e.getOrganizador().getUsername().equals(currentUser);

        var inscripciones = inscripcionRepo.findByEventoIdAndFiltro(idEvento, texto).stream()
            .map(i -> DTOInscripcionesEvento.InscripcionDTO.builder()
                    .id(i.getId())
                    .usuario(DTOInscripcionesEvento.UsuarioDTO.builder()
                            .username(i.getUsuario().getUsername())
                            .nombre(i.getUsuario().getNombre())
                            .apellido(i.getUsuario().getApellido())
                            .build())
                    .fechaInscripcion(i.getFechaHoraAlta())
                    .fechaCancelacionInscripcion(i.getFechaHoraBaja())
                    .transferencias(List.of()) // ⚠️ TODO: mapear entidad Transferencia
                    .invitados(i.getInvitados().stream()
                            .map(inv -> DTOInscripcionesEvento.InvitadoDTO.builder()
                                    .nombre(inv.getNombre())
                                    .apellido(inv.getApellido())
                                    .dni(inv.getDni())
                                    .build())
                            .toList())
                    .build())
            .toList();

        return DTOInscripcionesEvento.builder()
            .nombreEvento(e.getNombre())
            .esAdministrador(esAdministrador)
            .esOrganizador(esOrganizador)
            .inscripciones(inscripciones)
            .build();
        }



        @Override
        @Transactional
        public void cancelarInscripcion(long idInscripcion) {
        Inscripcion ins = inscripcionRepo.findById(idInscripcion)
                .orElseThrow(() -> new HttpErrorException(404, "Inscripción no encontrada"));

        // Guardar hora de baja con zona Argentina
        ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");
        ins.setFechaHoraBaja(LocalDateTime.now(zone));

        inscripcionRepo.save(ins);
        }


        @Override
        @Transactional
        public DTODatosParaInscripcion obtenerDatosParaInscripcion(long idEvento, String username) throws Exception {
                Evento e = eventoRepo.findById(idEvento)
                        .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        boolean esAdministrador = eventoRepo.existsByEventoIdAndAdministradorUsername(idEvento, username);
        boolean esOrganizador = e.getOrganizador() != null && e.getOrganizador().getUsername().equals(username);

        return DTODatosParaInscripcion.builder()
                .nombreEvento(e.getNombre())
                .cantidadMaximaInvitados(e.getCantidadMaximaInvitados())
                .limiteParticipantes(e.getCantidadMaximaParticipantes())
                .esAdministrador(esAdministrador)
                .esOrganizador(esOrganizador)
                .build();
        }

        @Override
        @Transactional
        public List<DTOBusquedaUsuario> buscarUsuariosNoInscriptos(Long idEvento, String texto) {
        return usuarioRepo.buscarNoInscriptos(idEvento, texto).stream()
                .map((Usuario u) -> DTOBusquedaUsuario.builder()
                        .username(u.getUsername())
                        .nombre(u.getNombre())
                        .apellido(u.getApellido())
                        .mail(u.getMail())
                        .dni(u.getDni())
                        .fechaNacimiento( u.getFechaNacimiento() != null ? u.getFechaNacimiento().toLocalDate() : null )
                        .build()
                )
                .toList();
        }



        @Override
        @Transactional
        public void inscribirUsuario(DTOInscripcion dto) {
        Evento e = eventoRepo.findById(dto.getIdEvento())
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        Usuario u = usuarioRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

        Inscripcion ins = new Inscripcion();
        ins.setEvento(e);
        ins.setUsuario(u);

        // Guardar hora de alta con zona Argentina
        ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");
        ins.setFechaHoraAlta(LocalDateTime.now(zone));

        if (dto.getPrecioInscripcion() != null) {
                ins.setPrecioInscripcion(dto.getPrecioInscripcion());
        }
        ins.setPermitirDevolucionCompleta(Boolean.FALSE);

        inscripcionRepo.save(ins);

        // Guardar invitados si vienen
        if (dto.getInvitados() != null) {
                for (DTOInscripcion.Invitado i : dto.getInvitados()) {
                Invitado inv = new Invitado();
                inv.setInscripcion(ins);
                inv.setNombre(i.getNombre());
                inv.setApellido(i.getApellido());
                inv.setDni(i.getDni());
                invitadoRepo.save(inv);
                }
            }
        }


        @Override
        @Transactional
        public DTOAdministradores obtenerAdministradores(long idEvento, String currentUser) throws Exception {
        Evento e = eventoRepo.findById(idEvento)
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        boolean esOrganizador = e.getOrganizador() != null &&
                e.getOrganizador().getUsername().equals(currentUser);

        var admins = e.getAdministradoresEvento().stream()
                .map(a -> DTOAdministradores.AdministradorDTO.builder()
                        .username(a.getUsuario().getUsername())
                        .nombre(a.getUsuario().getNombre())
                        .apellido(a.getUsuario().getApellido())
                        .vigente(a.getFechaHoraBaja() == null)
                        .historico(List.of(DTOAdministradores.HistoricoDTO.builder()
                                .fechaDesde(a.getFechaHoraAlta())
                                .fechaHasta(a.getFechaHoraBaja())
                                .build()))
                        .build())
                .toList();

        return DTOAdministradores.builder()
                .esOrganizador(esOrganizador)
                .nombreEvento(e.getNombre())
                .administradores(admins)
                .build();
        }

        @Override
        @Transactional
        public List<DTOBusquedaUsuario> buscarUsuariosNoAdministradores(Long idEvento, String texto) {
        return usuarioRepo.buscarUsuariosNoAdministradores(idEvento, texto).stream()
                .map((Usuario u) -> DTOBusquedaUsuario.builder()
                        .username(u.getUsername())
                        .nombre(u.getNombre())
                        .apellido(u.getApellido())
                        .mail(u.getMail())
                        .dni(u.getDni())
                        .fechaNacimiento( u.getFechaNacimiento() != null ? u.getFechaNacimiento().toLocalDate() : null )
                        .build()
                )
                .toList();
        }


        @Override
        @Transactional
        public void agregarAdministrador(long idEvento, String username) {
        Evento e = eventoRepo.findById(idEvento)
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));
        Usuario u = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

        // Evitar duplicados
        boolean yaEsAdmin = e.getAdministradoresEvento().stream()
                .anyMatch(a -> a.getUsuario().equals(u) && a.getFechaHoraBaja() == null);
        if (yaEsAdmin) return;

        AdministradorEvento ae = AdministradorEvento.builder()
                .evento(e)
                .usuario(u)
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        administradorEventoRepo.save(ae);
        }

        @Override
        @Transactional
        public void quitarAdministrador(long idEvento, String username) {
        AdministradorEvento ae = administradorEventoRepo
                .findByEventoIdAndUsuarioUsernameAndFechaHoraBajaIsNull(idEvento, username)
                .orElseThrow(() -> new HttpErrorException(404, "Administrador no encontrado o ya dado de baja"));

        ae.setFechaHoraBaja(LocalDateTime.now());
        administradorEventoRepo.save(ae);
        }

        @Override
        @Transactional
        public void entregarOrganizador(long idEvento, String nuevoOrganizador) throws Exception {
        Evento e = eventoRepo.findById(idEvento)
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        Usuario nuevo = usuarioRepo.findByUsername(nuevoOrganizador)
                .orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

        // El organizador actual pasa a ser administrador
        if (e.getOrganizador() != null) {
                Usuario anterior = e.getOrganizador();

                // Validar en DB si ya es administrador activo
                boolean yaEsAdmin = administradorEventoRepo.existeAdministradorActivo(
                        e.getId(), anterior.getId()
                );

                if (!yaEsAdmin) {
                AdministradorEvento ae = AdministradorEvento.builder()
                        .evento(e)
                        .usuario(anterior)
                        .fechaHoraAlta(LocalDateTime.now())
                        .build();
                administradorEventoRepo.save(ae);
                }
           }

        // Cambiar organizador
		// TO-DO
        //e.setOrganizador(nuevo);
        eventoRepo.save(e);
        }


        @Override
        @Transactional
        public void denunciarEvento(DTODenunciaEvento dto, String username) {
        Usuario denunciante = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

        Evento evento = eventoRepo.findById(dto.getIdEvento())
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        DenunciaEvento d = DenunciaEvento.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .denunciante(denunciante)
                .evento(evento)
                .build();

        denunciaEventoRepo.save(d);

        // Estado inicial
        EstadoDenunciaEvento estadoInicial = estadoDenunciaRepo.findAll().stream()
                .filter(e -> e.getNombre().equalsIgnoreCase("Ingresado"))
                .findFirst()
                .orElseThrow(() -> new HttpErrorException(400, "Estado inicial no configurado"));

        DenunciaEventoEstado dee = DenunciaEventoEstado.builder()
                .denunciaEvento(d)
                .estadoDenunciaEvento(estadoInicial)
                .fechaHoraDesde(LocalDateTime.now())
                .build();
        denunciaEventoEstadoRepo.save(dee);
        }

        @Override
        @Transactional
        public Page<DTODenunciaEventoSimple> buscarDenuncias(DTOBusquedaDenunciasEventos filtro, int page) throws Exception {
        	// TO-DO: Traer el pageSize de un parámetro del sistema
			
			Pageable pageable = PageRequest.of(page, 20, switch (filtro.getOrden()) {
                case FECHA_DENUNCIA_ASC -> Sort.by("fechaHoraAlta").ascending();
                case FECHA_DENUNCIA_DESC -> Sort.by("fechaHoraAlta").descending();
                case FECHA_CAMBIO_ESTADO_ASC -> Sort.by("estados.fechaHoraDesde").ascending();
                case FECHA_CAMBIO_ESTADO_DESC -> Sort.by("estados.fechaHoraDesde").descending();
        	});

        	return denunciaEventoRepo.findAll(DenunciaEventoSpecs.byFiltro(filtro), pageable)
                .map(d -> {
					String organizador = "";

					try {
						organizador = d.getEvento().getOrganizador().getUsername();
					} catch (Exception e) {
						organizador = "?";
					}

					return DTODenunciaEventoSimple.builder()
					.idDenuncia(d.getId())
					.titulo(d.getTitulo())
					.usernameDenunciante(d.getDenunciante().getUsername())
					.nombreEvento(d.getEvento().getNombre())
					.usernameOrganizador(organizador)
					.estado(d.getEstados().isEmpty()
							? "SIN_ESTADO"
							: d.getEstados().get(d.getEstados().size() - 1).getEstadoDenunciaEvento().getNombre())
					.fechaHoraUltimoCambio(d.getEstados().isEmpty()
							? null
							: d.getEstados().get(d.getEstados().size() - 1).getFechaHoraDesde())
					// ✅ Usamos la fecha del primer estado como "ingreso" de la denuncia
					.fechaHoraIngreso(d.getEstados().isEmpty()
							? null
							: d.getEstados().get(0).getFechaHoraDesde())
					.build();
				});
        }



        @Override
        @Transactional
        public DTODenunciaEventoCompleta obtenerDenunciaCompleta(long idDenuncia) throws Exception {
        DenunciaEvento d = denunciaEventoRepo.findById(idDenuncia)
                .orElseThrow(() -> new HttpErrorException(404, "Denuncia no encontrada"));

        return DTODenunciaEventoCompleta.builder()
                .id(d.getId())
                .titulo(d.getTitulo())
                .descripcion(d.getDescripcion())
                .denunciante(DTODenunciaEventoCompleta.DenuncianteDTO.builder()
                        .nombre(d.getDenunciante().getNombre())
                        .apellido(d.getDenunciante().getApellido())
                        .username(d.getDenunciante().getUsername())
                        .mail(d.getDenunciante().getMail())
                        .build())
                .historico(d.getEstados().stream().map(e ->
                        DTODenunciaEventoCompleta.HistoricoDTO.builder()
                                .nombre(e.getEstadoDenunciaEvento().getNombre())
                                .fechaHoraDesde(e.getFechaHoraDesde())
                                .descripcion(e.getDescripcion())
                                .responsable(e.getResponsable() != null ?
                                        DTODenunciaEventoCompleta.ResponsableDTO.builder()
                                                .nombre(e.getResponsable().getNombre())
                                                .apellido(e.getResponsable().getApellido())
                                                .username(e.getResponsable().getUsername())
                                                .mail(e.getResponsable().getMail())
                                                .build()
                                        : null)
                                .build()
                ).toList())
                .evento(DTODenunciaEventoCompleta.EventoDTO.builder()
                        .id(d.getEvento().getId())
                        .nombre(d.getEvento().getNombre())
                        .descripcion(d.getEvento().getDescripcion())
                        .espacio(DTODenunciaEventoCompleta.EspacioDTO.builder()
							.nombre(d.getEvento().getSubEspacio().getEspacio().getNombre())
							.direccion(d.getEvento().getSubEspacio().getEspacio().getDireccionUbicacion())
							.build())
                        .fechaHoraInicio(d.getEvento().getFechaHoraInicio())
                        .fechaHoraFin(d.getEvento().getFechaHoraFin())
                        .participantes(d.getEvento().getInscripciones() != null ? d.getEvento().getInscripciones().size() : 0)
                        .organizador(DTODenunciaEventoCompleta.OrganizadorDTO.builder()
                                .nombre(d.getEvento().getOrganizador().getNombre())
                                .apellido(d.getEvento().getOrganizador().getApellido())
                                .username(d.getEvento().getOrganizador().getUsername())
                                .mail(d.getEvento().getOrganizador().getMail())
                                .build())
                        .administradores(d.getEvento().getAdministradoresEvento().stream().map(a ->
                                DTODenunciaEventoCompleta.AdministradorDTO.builder()
                                        .nombre(a.getUsuario().getNombre())
                                        .apellido(a.getUsuario().getApellido())
                                        .username(a.getUsuario().getUsername())
                                        .mail(a.getUsuario().getMail())
                                        .build()
                        ).toList())
                        .build())
                .build();
        }

        @Override
        @Transactional
        public DTODatosParaCambioEstadoDenuncia obtenerDatosParaCambioEstado(long idDenuncia) {
        DenunciaEvento d = denunciaEventoRepo.findById(idDenuncia)
                .orElseThrow(() -> new HttpErrorException(404, "Denuncia no encontrada"));

                var estados = estadoDenunciaRepo.findAll().stream()
                .map(e -> DTODatosParaCambioEstadoDenuncia.EstadoDTO.builder()
                        .id(e.getId())
                        .nombre(e.getNombre())
                        .build())
                .toList();
        

        return DTODatosParaCambioEstadoDenuncia.builder()
                .titulo(d.getTitulo())
                .estados(estados)
                .build();
        }

        @Override
        @Transactional
        public void cambiarEstadoDenuncia(DTOCambioEstadoDenuncia dto, String username) {
        DenunciaEvento d = denunciaEventoRepo.findById(dto.getIdDenuncia())
                .orElseThrow(() -> new HttpErrorException(404, "Denuncia no encontrada"));

        Usuario responsable = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

        EstadoDenunciaEvento nuevoEstado = estadoDenunciaRepo.findById(dto.getEstado())
                .orElseThrow(() -> new HttpErrorException(404, "Estado no encontrado"));

        // cerrar último estado
        d.getEstados().stream().filter(e -> e.getFechaHoraHasta() == null).forEach(e -> {
                e.setFechaHoraHasta(LocalDateTime.now());
                denunciaEventoEstadoRepo.save(e);
        });

        // agregar nuevo
        DenunciaEventoEstado dee = DenunciaEventoEstado.builder()
                .denunciaEvento(d)
                .estadoDenunciaEvento(nuevoEstado)
                .descripcion(dto.getDescripcion())
                .responsable(responsable)
                .fechaHoraDesde(LocalDateTime.now())
                .build();

        denunciaEventoEstadoRepo.save(dee);
        }

        @Override
        @Transactional
        public DTODatosParaDenunciarEvento obtenerDatosParaDenunciar(long idEvento, String username) {
        Evento e = eventoRepo.findById(idEvento)
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        boolean inscripto = inscripcionRepo.countByEventoIdAndUsuarioUsername(idEvento, username) > 0;
        boolean hayDenunciaPrevia = !denunciaEventoRepo.findAll().stream()
                .filter(d -> d.getEvento().getId().equals(idEvento) && d.getDenunciante().getUsername().equals(username))
                .toList().isEmpty();

        return DTODatosParaDenunciarEvento.builder()
                .nombre(e.getNombre())
                .inscripto(inscripto)
                .fechaDesde(e.getFechaHoraInicio())
                .hayDenunciaPrevia(hayDenunciaPrevia)
                .build();
        }


    private static int[] splitMinutes(Integer minutos) {
        int total = (minutos != null) ? Math.max(0, minutos) : 0;
        int dias = Math.floorDiv(total, 1440);
        int rem = total - dias * 1440;
        int horas = Math.floorDiv(rem, 60);
        int mins = rem - horas * 60;
        return new int[]{dias, horas, mins};
    }
}
