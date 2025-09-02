package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.*;
import com.evtnet.evtnetback.Repositories.*;
import com.evtnet.evtnetback.Repositories.specs.EventoSpecs;
import com.evtnet.evtnetback.dto.disciplinaevento.DTODisciplinaEventoCreate;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.error.HttpErrorException;
import com.evtnet.evtnetback.mapper.EventoSearchMapper; // ← tu mapper manual renombrado
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.evtnet.evtnetback.dto.eventos.DTOEventoCreate;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventoServiceImpl extends BaseServiceImpl<Evento, Long> implements EventoService {

    private final EventoRepository eventoRepo;
    private final DisciplinaEventoRepository disciplinaEventoRepo; // hijos
    private final DisciplinaRepository disciplinaBaseRepo;          // master de disciplinas
    private final ModoEventoRepository modoRepo;
    private final TipoInscripcionEventoRepository tipoInscripcionRepo;
    private final EspacioRepository espacioRepo;
    private final EventoModoEventoRepository eventoModoEventoRepo;
    private final InscripcionRepository inscripcionRepo;
    private final AdministradorEventoRepository administradorEventoRepo;

    public EventoServiceImpl(
            EventoRepository eventoRepo,
            DisciplinaEventoRepository disciplinaEventoRepo,
            DisciplinaRepository disciplinaBaseRepo,
            ModoEventoRepository modoRepo,
            TipoInscripcionEventoRepository tipoInscripcionRepo,
            EspacioRepository espacioRepo,
            EventoModoEventoRepository eventoModoEventoRepo,
            InscripcionRepository inscripcionRepo,
            AdministradorEventoRepository administradorEventoRepo
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
    }

    @Override
    @Transactional
    public List<DTOResultadoBusquedaEventos> buscar(DTOBusquedaEventos filtro) {
        return eventoRepo.findAll(EventoSpecs.byFiltroBusqueda(filtro), Sort.by("fechaHoraInicio").ascending())
                .stream().map(EventoSearchMapper::toResultadoBusqueda).toList();
    }

    @Override
    @Transactional
    public List<DTOResultadoBusquedaMisEventos> buscarMisEventos(DTOBusquedaMisEventos filtro) {
        return eventoRepo.findAll(EventoSpecs.byFiltroMisEventos(filtro), Sort.by("fechaHoraInicio").descending())
                .stream().map(EventoSearchMapper::toResultadoBusquedaMis).toList();
    }

    @Override
    @Transactional
    public DTOEvento obtenerEventoDetalle(long idEvento) {
        Evento e = eventoRepo.findByIdFetchAll(idEvento)
                .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

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
        // Validaciones mínimas (podés ampliar con Bean Validation)
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
        if (r.getSuperEventoId() != null) {
            // Si usás super-eventos:
            // SuperEvento se = superEventoRepo.findById(r.getSuperEventoId()).orElseThrow(...);
            // e.setSuperEvento(se);
        }

        // 🔹 Crear hijos DisciplinaEvento a partir del DTO
        List<DisciplinaEvento> hijos = new ArrayList<>();
        if (r.getDisciplinasEvento() != null) {
            for (DTODisciplinaEventoCreate deDto : r.getDisciplinasEvento()) {
                Long disciplinaId = (deDto.getDisciplina() != null) ? deDto.getDisciplina().getId() : null;
                if (disciplinaId == null) {
                    throw new HttpErrorException(400, "disciplina.id es requerido en cada disciplinasEvento");
                }
                Disciplina disciplina = disciplinaBaseRepo.findById(disciplinaId)
                        .orElseThrow(() -> new HttpErrorException(400, "Disciplina no encontrada: id=" + disciplinaId));

                DisciplinaEvento de = DisciplinaEvento.builder()
                        .evento(e)
                        .disciplina(disciplina)
                        .nombre(deDto.getNombre() != null ? deDto.getNombre() : disciplina.getNombre())
                        .descripcion(deDto.getDescripcion())
                        .build();
                hijos.add(de);
            }
        }
        e.setDisciplinasEvento(hijos); // cascade = ALL + orphanRemoval en la entity

        // Guardar evento (persiste hijos por cascade)
        eventoRepo.save(e);

        // Si querés además registrar en tabla puente EventoModoEvento según "modoEventoId" u otros modos extra
        // (por ahora no hay lista de modos en DTOEventoCreate, así que dejamos solo el principal)

        return e.getId();
    }

    @Override
    @Transactional
    public int obtenerCantidadEventosSuperpuestos(long idEspacio, long fechaDesdeMillis, long fechaHastaMillis) {
        LocalDateTime desde = LocalDateTime.ofEpochSecond(fechaDesdeMillis / 1000, 0, java.time.ZoneOffset.UTC);
        LocalDateTime hasta = LocalDateTime.ofEpochSecond(fechaHastaMillis / 1000, 0, java.time.ZoneOffset.UTC);
        return eventoRepo.contarSuperpuestosPorEspacio(idEspacio, desde, hasta);
    }
}
