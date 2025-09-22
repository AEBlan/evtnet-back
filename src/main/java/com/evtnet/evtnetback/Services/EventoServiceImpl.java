// src/main/java/com/evtnet/evtnetback/Services/EventoServiceImpl.java
package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.*;
import com.evtnet.evtnetback.Repositories.*;
import com.evtnet.evtnetback.Repositories.specs.EventoSpecs;
import com.evtnet.evtnetback.dto.disciplinaevento.DTODisciplinaEventoCreate;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.error.HttpErrorException;
import com.evtnet.evtnetback.mapper.EventoSearchMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.Comparator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class EventoServiceImpl extends BaseServiceImpl<Evento, Long> implements EventoService {

    private final EventoRepository eventoRepo;
    private final DisciplinaEventoRepository disciplinaEventoRepo;
    private final DisciplinaRepository disciplinaBaseRepo;
    private final ModoEventoRepository modoRepo;
    private final TipoInscripcionEventoRepository tipoInscripcionRepo;
    private final EspacioRepository espacioRepo;
    private final EventoModoEventoRepository eventoModoEventoRepo;
    private final InscripcionRepository inscripcionRepo;
    private final AdministradorEventoRepository administradorEventoRepo;
    private final UsuarioRepository usuarioRepo;
    private final ComprobantePagoRepository comprobanteRepo;
    private final InvitadoRepository invitadoRepo;

    public EventoServiceImpl(
            EventoRepository eventoRepo,
            DisciplinaEventoRepository disciplinaEventoRepo,
            DisciplinaRepository disciplinaBaseRepo,
            ModoEventoRepository modoRepo,
            TipoInscripcionEventoRepository tipoInscripcionRepo,
            EspacioRepository espacioRepo,
            EventoModoEventoRepository eventoModoEventoRepo,
            InscripcionRepository inscripcionRepo,
            AdministradorEventoRepository administradorEventoRepo,
            UsuarioRepository usuarioRepo,
            ComprobantePagoRepository comprobanteRepo,
            InvitadoRepository invitadoRepo
    ) {
        super(eventoRepo);
        this.eventoRepo = eventoRepo;
        this.disciplinaEventoRepo = disciplinaEventoRepo;
        this.disciplinaBaseRepo = disciplinaBaseRepo;
        this.modoRepo = modoRepo;
        this.tipoInscripcionRepo = tipoInscripcionRepo;
        this.espacioRepo = espacioRepo;
        this.eventoModoEventoRepo = eventoModoEventoRepo;
        this.inscripcionRepo = inscripcionRepo;
        this.administradorEventoRepo = administradorEventoRepo;
        this.usuarioRepo = usuarioRepo;
        this.comprobanteRepo = comprobanteRepo;
        this.invitadoRepo = invitadoRepo;
    }

    @Override
    @Transactional
    public List<DTOResultadoBusquedaEventos> buscar(DTOBusquedaEventos filtro) {
        return eventoRepo.findAll(EventoSpecs.byFiltroBusqueda(filtro), Sort.by("fechaHoraInicio").ascending())
                .stream().map(EventoSearchMapper::toResultadoBusqueda).toList();
    }

    @Override
    @Transactional
    public List<DTOResultadoBusquedaMisEventos> buscarMisEventos(DTOBusquedaMisEventos filtro, String username) {
        return eventoRepo.findAll(EventoSpecs.byFiltroMisEventos(filtro, username),
                              Sort.by("fechaHoraInicio").descending())
            .stream()
            .map(EventoSearchMapper::toResultadoBusquedaMis)
            .toList();
    }

    @Override
    @Transactional
    public DTOEvento obtenerEventoDetalle(long idEvento) {
        // 🔁 Usa el query que NO hace fetch de dos bolsas
        Evento e = eventoRepo.findByIdForDetalle(idEvento)
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        // (Opcional) tocar colecciones LAZY para inicializarlas dentro de la TX
        if (e.getEventosModoEvento() != null)
            e.getEventosModoEvento().forEach(eme -> { if (eme.getModoEvento()!=null) eme.getModoEvento().getNombre(); });
        if (e.getInscripciones() != null)
            e.getInscripciones().forEach(i -> { if (i.getUsuario()!=null) i.getUsuario().getUsername(); });

        String username = null;
        try { username = SecurityContextHolder.getContext().getAuthentication().getName(); }
        catch (Exception ignored) {}

        boolean inscripto = (username != null) &&
                inscripcionRepo.countByEventoIdAndUsuarioUsername(e.getId(), username) > 0;

        boolean administrador = false;
        if (username != null) {
            try {
                administrador = eventoRepo.existsByEventoIdAndAdministradorUsername(e.getId(), username);
            } catch (Exception ignored) { administrador = false; }
        }

        return EventoSearchMapper.toDTOEvento(e, inscripto, administrador);
    }

    @Override
    @Transactional
    public DTODatosCreacionEvento obtenerDatosCreacionEvento(Long idEspacioOrNull) {
        String nombreEspacio = null;
        if (idEspacioOrNull != null) {
            Espacio esp = espacioRepo.findById(idEspacioOrNull)
                    .orElseThrow(() -> new HttpErrorException(404, "Espacio no encontrado"));
            nombreEspacio = esp.getNombre();
        }

        var tipos = tipoInscripcionRepo.findAll().stream()
                .map(t -> new DTODatosCreacionEvento.TipoInscripcion(t.getId(), t.getNombre()))
                .toList();

        double comision = 0.12;
        int diasHaciaAdelante = 30;

        return new DTODatosCreacionEvento(nombreEspacio, tipos, comision, null, null, diasHaciaAdelante);
    }

    @Override
    @Transactional
    public long crearEvento(DTOEventoCreate r) {
    if (r.getFechaHoraInicio() == null || r.getFechaHoraFin() == null) {
        throw new HttpErrorException(400, "Fecha/hora de inicio y fin son requeridas");
    }

    Evento e = new Evento();
    e.setNombre(r.getNombre());
    e.setDescripcion(r.getDescripcion());
    e.setFechaHoraInicio(r.getFechaHoraInicio());
    e.setFechaHoraFin(r.getFechaHoraFin());
    e.setDireccionUbicacion(r.getDireccionUbicacion());
    e.setLatitudUbicacion(r.getLatitudUbicacion());
    e.setLongitudUbicacion(r.getLongitudUbicacion());
    e.setPrecioInscripcion(r.getPrecioInscripcion());
    e.setCantidadMaximaInvitados(r.getCantidadMaximaInvitados());
    e.setCantidadMaximaParticipantes(r.getCantidadMaximaParticipantes());
    e.setPrecioOrganizacion(r.getPrecioOrganizacion());

    // 🔹 Asignar organizador (usuario autenticado)
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Usuario organizador = usuarioRepo.findByUsername(username)
            .orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));
    e.setOrganizador(organizador);

    if (r.getEspacioId() != null) {
        Espacio esp = espacioRepo.findById(r.getEspacioId())
                .orElseThrow(() -> new HttpErrorException(404, "Espacio no encontrado"));
        e.setEspacio(esp);
    }
    if (r.getTipoInscripcionEventoId() != null) {
        TipoInscripcionEvento tie = tipoInscripcionRepo.findById(r.getTipoInscripcionEventoId())
                .orElseThrow(() -> new HttpErrorException(400, "Tipo de inscripción inválido"));
        e.setTipoInscripcionEvento(tie);
    }
    if (r.getModoEventoId() != null) {
        ModoEvento me = modoRepo.findById(r.getModoEventoId())
                .orElseThrow(() -> new HttpErrorException(400, "Modo de evento inválido"));
        e.setModoEvento(me);
    }
    if (r.getAdministradorEventoId() != null) {
        AdministradorEvento adm = administradorEventoRepo.findById(r.getAdministradorEventoId())
                .orElseThrow(() -> new HttpErrorException(400, "Administrador de evento inválido"));
        e.getAdministradoresEvento().add(adm);
    }

    // Hijos DisciplinaEvento
    List<DisciplinaEvento> hijos = new ArrayList<>();
    if (r.getDisciplinasEvento() != null) {
        for (DTODisciplinaEventoCreate deDto : r.getDisciplinasEvento()) {
            Long disciplinaId = (deDto.getDisciplina() != null) ? deDto.getDisciplina().getId() : null;
            if (disciplinaId == null) {
                throw new HttpErrorException(400, "disciplina.id es requerido en cada disciplinasEvento");
            }
            Disciplina disciplina = disciplinaBaseRepo.findById(disciplinaId)
                    .orElseThrow(() -> new HttpErrorException(400, "Disciplina no encontrada: id=" + disciplinaId));

            hijos.add(DisciplinaEvento.builder().evento(e).disciplina(disciplina).build());
        }
    }
    e.setDisciplinasEvento(hijos);

    eventoRepo.save(e);
    return e.getId();
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
        if (e.getEspacio() != null) {
            espacio = DTOEventoParaInscripcion.Espacio.builder()
                    .id(e.getEspacio().getId())
                    .nombre(e.getEspacio().getNombre())
                    .descripcion(e.getEspacio().getDescripcion())
                    .build();
        }

        return DTOEventoParaInscripcion.builder()
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .idSuperevento(e.getSuperEvento() != null ? e.getSuperEvento().getId() : null)
                .fechaDesde(e.getFechaHoraInicio())
                .fechaHasta(e.getFechaHoraFin())
                .espacio(espacio)
                .direccion(e.getDireccionUbicacion())
                .ubicacion(new DTOEventoParaInscripcion.Ubicacion(
                        e.getLatitudUbicacion() != null ? e.getLatitudUbicacion().doubleValue() : null,
                        e.getLongitudUbicacion() != null ? e.getLongitudUbicacion().doubleValue() : null))
                .precioPorAsistente(e.getPrecioInscripcion())
                .cantidadMaximaInvitados(e.getCantidadMaximaInvitados())
                .limiteParticipantes(e.getCantidadMaximaParticipantes())
                .build();
    }

    @Override @Transactional
    public boolean verificarDatosPrePago(DTOInscripcion dto) {
        Evento e = eventoRepo.findById(dto.getIdEvento())
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        String username = resolveUsername(dto.getUsername()); // ← tomar del token si viene vacío

        if (inscripcionRepo.countByEventoIdAndUsuarioUsername(e.getId(), username) > 0) return false;
        if (LocalDateTime.now().isAfter(e.getFechaHoraInicio())) return false;

        int actuales = inscripcionRepo.countParticipantesEfectivos(e.getId());
        int nuevos = 1 + (dto.getInvitados() != null ? dto.getInvitados().size() : 0);
        Integer limite = e.getCantidadMaximaParticipantes();
        if (limite != null && actuales + nuevos > limite) return false;

        if (e.getCantidadMaximaInvitados() != null && dto.getInvitados() != null &&
                dto.getInvitados().size() > e.getCantidadMaximaInvitados()) return false;

        if (dto.getPrecioInscripcion() != null && e.getPrecioInscripcion() != null &&
                dto.getPrecioInscripcion().compareTo(e.getPrecioInscripcion()) < 0) return false;

        return true;
    }


    @Override @Transactional
    public void inscribirse(DTOInscripcion dto) {
        if (!verificarDatosPrePago(dto))
            throw new HttpErrorException(400, "Datos inválidos para inscribirse");

        Evento e = eventoRepo.findById(dto.getIdEvento())
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        String username = resolveUsername(dto.getUsername()); // ← token si falta
        Usuario u = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

        Inscripcion ins = new Inscripcion();
        ins.setEvento(e);
        ins.setUsuario(u);
        ins.setFechaHoraAlta(LocalDateTime.now());
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

    // (opcional) persistir dto.getDatosPago() → comprobantes
    }


    @Override @Transactional
    public void desinscribirse(long idEvento) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Inscripcion ins = inscripcionRepo.findByEventoIdAndUsuarioUsername(idEvento, username)
                .orElseThrow(() -> new HttpErrorException(404, "Inscripción no encontrada"));
        invitadoRepo.deleteByInscripcionId(ins.getId());
        inscripcionRepo.delete(ins);
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

    @Override @Transactional
    public DTOModificarEvento obtenerDatosModificacionEvento(long idEvento) {
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

        List<DTOModificarEvento.ItemIdNombre> modos = new ArrayList<>();
        if (e.getModoEvento() != null)
            modos.add(new DTOModificarEvento.ItemIdNombre(e.getModoEvento().getId(), e.getModoEvento().getNombre()));
        if (e.getEventosModoEvento() != null)
            for (EventoModoEvento eme : e.getEventosModoEvento())
                modos.add(new DTOModificarEvento.ItemIdNombre(eme.getModoEvento().getId(), eme.getModoEvento().getNombre()));

        List<DTOModificarEvento.TipoInscripcion> tipos = tipoInscripcionRepo.findAll().stream()
                .map(t -> DTOModificarEvento.TipoInscripcion.builder()
                        .id(t.getId())
                        .nombre(t.getNombre())
                        .seleccionado(e.getTipoInscripcionEvento() != null &&
                                Objects.equals(e.getTipoInscripcionEvento().getId(), t.getId()))
                        .build())
                .toList();

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

        return DTOModificarEvento.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .descripcion(e.getDescripcion())
                .idEspacio(e.getEspacio() != null ? e.getEspacio().getId() : null)
                .nombreEspacio(e.getEspacio() != null ? e.getEspacio().getNombre() : null)
                .usarCronograma(false)
                .fechaDesde(e.getFechaHoraInicio())
                .fechaHasta(e.getFechaHoraFin())
                .horarioId(null)
                .precioOrganizacion(e.getPrecioOrganizacion())
                .direccion(e.getDireccionUbicacion())
                .ubicacion(new DTOModificarEvento.Ubicacion(
                        e.getLatitudUbicacion() != null ? e.getLatitudUbicacion().doubleValue() : null,
                        e.getLongitudUbicacion() != null ? e.getLongitudUbicacion().doubleValue() : null))
                .disciplinas(disciplinas)
                .modos(modos)
                .tiposInscripcion(tipos)
                .precioInscripcion(e.getPrecioInscripcion())
                .comisionInscripcion(BigDecimal.valueOf(0.12))
                .cantidadMaximaParticipantes(e.getCantidadMaximaParticipantes())
                .cantidadMaximaInvitados(e.getCantidadMaximaInvitados())
                .cantidadParticipantesActual(participantes)
                .cantidadMaximaInvitadosPorInvitacionEfectiva(maxInvPorInscripcion)
                .crearSuperevento(null)
                .superevento(e.getSuperEvento() != null
                        ? new DTOModificarEvento.Superevento(e.getSuperEvento().getId(),
                                                             e.getSuperEvento().getNombre(),
                                                             e.getSuperEvento().getDescripcion())
                        : null)
                .rangosReintegro(rangos)
                .espacioPublico(null)
                .administradorEspacio(null)
                .administradorEvento(null)
                .organizadorEvento(true)
                .diasHaciaAdelante(30)
                .build();
    }

    @Override @Transactional
    public void modificarEvento(DTOModificarEvento dto) {
        Evento e = eventoRepo.findById(dto.getId())
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        if (dto.getFechaDesde() == null || dto.getFechaHasta() == null)
            throw new HttpErrorException(400, "Fechas requeridas");

        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        e.setFechaHoraInicio(dto.getFechaDesde());
        e.setFechaHoraFin(dto.getFechaHasta());
        e.setDireccionUbicacion(dto.getDireccion());
        if (dto.getUbicacion() != null) {
            e.setLatitudUbicacion(dto.getUbicacion().getLatitud() != null ?
                    BigDecimal.valueOf(dto.getUbicacion().getLatitud()) : null);
            e.setLongitudUbicacion(dto.getUbicacion().getLongitud() != null ?
                    BigDecimal.valueOf(dto.getUbicacion().getLongitud()) : null);
        }
        e.setPrecioInscripcion(dto.getPrecioInscripcion());
        e.setPrecioOrganizacion(dto.getPrecioOrganizacion());
        e.setCantidadMaximaInvitados(dto.getCantidadMaximaInvitados());
        e.setCantidadMaximaParticipantes(dto.getCantidadMaximaParticipantes());

        if (dto.getIdEspacio() != null) {
            e.setEspacio(espacioRepo.findById(dto.getIdEspacio())
                    .orElseThrow(() -> new HttpErrorException(404, "Espacio no encontrado")));
        } else {
            e.setEspacio(null);
        }

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

        if (dto.getTiposInscripcion() != null) {
            dto.getTiposInscripcion().stream().filter(DTOModificarEvento.TipoInscripcion::isSeleccionado).findFirst()
                    .ifPresent(sel -> e.setTipoInscripcionEvento(
                            tipoInscripcionRepo.findById(sel.getId())
                                    .orElseThrow(() -> new HttpErrorException(400, "Tipo inscripción inválido"))));
        }

        if (dto.getModos() != null && !dto.getModos().isEmpty()) {
            ModoEvento principal = modoRepo.findById(dto.getModos().get(0).getId())
                    .orElseThrow(() -> new HttpErrorException(400, "Modo de evento inválido"));
            e.setModoEvento(principal);

            if (e.getEventosModoEvento() == null) e.setEventosModoEvento(new ArrayList<>());
            e.getEventosModoEvento().clear();
            for (int i = 1; i < dto.getModos().size(); i++) {
                ModoEvento me = modoRepo.findById(dto.getModos().get(i).getId())
                        .orElseThrow(() -> new HttpErrorException(400, "Modo de evento inválido"));
                e.getEventosModoEvento().add(EventoModoEvento.builder().evento(e).modoEvento(me).build());
            }
        }

        eventoRepo.save(e);
    }

    private static int[] splitMinutes(Integer minutos) {
        int total = (minutos != null) ? Math.max(0, minutos) : 0;
        int dias = Math.floorDiv(total, 1440);
        int rem = total - dias * 1440;
        int horas = Math.floorDiv(rem, 60);
        int mins = rem - horas * 60;
        return new int[]{dias, horas, mins};
    }

    private String resolveUsername(String maybeUsername) {
        String u = (maybeUsername == null) ? "" : maybeUsername.trim();
        if (!u.isEmpty()) return u;
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new HttpErrorException(401, "No hay usuario autenticado");
        }
        return auth.getName();
    }

}
