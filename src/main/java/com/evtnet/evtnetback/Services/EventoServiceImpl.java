package com.evtnet.evtnetback.Services;
import com.evtnet.evtnetback.Entities.*;
import com.evtnet.evtnetback.Repositories.*;
import com.evtnet.evtnetback.Repositories.specs.EventoSpecs;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.error.HttpErrorException;
import com.evtnet.evtnetback.mapper.EventoMapper;
import com.evtnet.evtnetback.utils.TimeUtil;
import jakarta.transaction.Transactional; // usa jakarta para ser consistente con tu base
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoServiceImpl extends BaseServiceImpl<Evento, Long> implements EventoService {

    private final EventoRepository eventoRepo;
    private final DisciplinaEventoRepository disciplinaRepo;
    private final ModoEventoRepository modoRepo;
    private final TipoInscripcionEventoRepository tipoInscripcionRepo;
    private final EspacioRepository espacioRepo;
    private final EventoModoEventoRepository eventoModoEventoRepo;
    private final InscripcionRepository inscripcionRepo;
    // Si aún no lo tenés o tu entity no referencia Usuario, podés comentar esta línea y el uso
    private final AdministradorEventoRepository administradorEventoRepo;

    // ÚNICO constructor (sin Lombok). Llama al super con el BaseRepository.
    public EventoServiceImpl(
            EventoRepository eventoRepo,
            DisciplinaEventoRepository disciplinaRepo,
            ModoEventoRepository modoRepo,
            TipoInscripcionEventoRepository tipoInscripcionRepo,
            EspacioRepository espacioRepo,
            EventoModoEventoRepository eventoModoEventoRepo,
            InscripcionRepository inscripcionRepo,
            AdministradorEventoRepository administradorEventoRepo
    ) {
        super(eventoRepo);
        this.eventoRepo = eventoRepo;
        this.disciplinaRepo = disciplinaRepo;
        this.modoRepo = modoRepo;
        this.tipoInscripcionRepo = tipoInscripcionRepo;
        this.espacioRepo = espacioRepo;
        this.eventoModoEventoRepo = eventoModoEventoRepo;
        this.inscripcionRepo = inscripcionRepo;
        this.administradorEventoRepo = administradorEventoRepo;
    }

    @Override
    @Transactional
    public List<DTOResultadoBusquedaEventos> buscar(DTOBusquedaEventos filtro) {
        return eventoRepo.findAll(EventoSpecs.byFiltroBusqueda(filtro), Sort.by("fechaHoraInicio").ascending())
                .stream().map(EventoMapper::toResultadoBusqueda).toList();
    }

    @Override
    @Transactional
    public List<DTOResultadoBusquedaMisEventos> buscarMisEventos(DTOBusquedaMisEventos filtro) {
        return eventoRepo.findAll(EventoSpecs.byFiltroMisEventos(filtro), Sort.by("fechaHoraInicio").descending())
                .stream().map(EventoMapper::toResultadoBusquedaMis).toList();
    }

    @Override
    @Transactional
    public DTOEvento obtenerEventoDetalle(long idEvento) {
        Evento e = eventoRepo.findByIdFetchAll(idEvento)
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

        String username = null;
        try {
            username = SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception ignored) {}

        boolean inscripto = (username != null) &&
                inscripcionRepo.countByEventoIdAndUsuarioUsername(e.getId(), username) > 0;

        boolean administrador = false;
        if (username != null) {
            try {
                // Verificar si el usuario es administrador del evento
                administrador = eventoRepo.existsByEventoIdAndAdministradorUsername(e.getId(), username);
            } catch (Exception ignored) {
                administrador = false;
            }
        }

        return EventoMapper.toDTOEvento(e, inscripto, administrador);
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
    public long crearEvento(DTOCrearEvento r) {
        if (r.usarCronograma() && (r.fechaDesde() == null || r.fechaHasta() == null)) {
            throw new HttpErrorException(400, "Faltan fechas para crear el evento");
        }

        Evento e = new Evento();
        e.setNombre(r.nombre());
        e.setDescripcion(r.descripcion());
        e.setFechaHoraInicio(TimeUtil.fromMillis(r.fechaDesde()));
        e.setFechaHoraFin(TimeUtil.fromMillis(r.fechaHasta()));
        e.setDireccionUbicacion(r.direccion());
        if (r.ubicacion() != null) {
            if (r.ubicacion().latitud() != null)
                e.setLatitudUbicacion(BigDecimal.valueOf(r.ubicacion().latitud()));
            if (r.ubicacion().longitud() != null)
                e.setLongitudUbicacion(BigDecimal.valueOf(r.ubicacion().longitud()));
        }
        e.setPrecioInscripcion(BigDecimal.valueOf(r.precio()));
        e.setCantidadMaximaParticipantes(r.maxParticipantes());

        if (r.idEspacio() != null) {
            Espacio esp = espacioRepo.findById(r.idEspacio())
                    .orElseThrow(() -> new HttpErrorException(404, "Espacio no encontrado"));
            e.setEspacio(esp);
            // TODO validar horarioId vs cronograma si usarCronograma = true
        }

        var tipo = tipoInscripcionRepo.findById(r.tipoInscripcion())
                .orElseThrow(() -> new HttpErrorException(400, "Tipo de inscripción inválido"));
        e.setTipoInscripcionEvento(tipo);

        if (r.disciplinas() != null && !r.disciplinas().isEmpty()) {
            var list = disciplinaRepo.findAllById(r.disciplinas());
            e.setDisciplinasEvento(list);
            if (!list.isEmpty()) e.setDisciplinaEvento(list.get(0)); // principal opcional
        }

        if (r.modos() != null && !r.modos().isEmpty()) {
            var modos = modoRepo.findAllById(r.modos());
            if (!modos.isEmpty()) e.setModoEvento(modos.get(0)); // principal opcional
        }

        eventoRepo.save(e);

        if (r.modos() != null && !r.modos().isEmpty()) {
            var modos = modoRepo.findAllById(r.modos());
            for (ModoEvento m : modos) {
                EventoModoEvento eme = EventoModoEvento.builder()
                        .evento(e)
                        .modoEvento(m)
                        .build();
                eventoModoEventoRepo.save(eme);
            }
        }

        return e.getId();
    }

    @Override
    @Transactional
    public int obtenerCantidadEventosSuperpuestos(long idEspacio, long fechaDesdeMillis, long fechaHastaMillis) {
        LocalDateTime desde = TimeUtil.fromMillis(fechaDesdeMillis);
        LocalDateTime hasta = TimeUtil.fromMillis(fechaHastaMillis);
        return eventoRepo.contarSuperpuestosPorEspacio(idEspacio, desde, hasta);
    }
}
