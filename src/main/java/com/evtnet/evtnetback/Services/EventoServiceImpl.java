// package com.evtnet.evtnetback.Services;
//
// import com.evtnet.evtnetback.Entities.*;
// import com.evtnet.evtnetback.Repositories.*;
// import com.evtnet.evtnetback.Repositories.specs.EventoSpecs;
// import com.evtnet.evtnetback.dto.disciplinaevento.DTODisciplinaEventoCreate;
// import com.evtnet.evtnetback.dto.eventos.*;
// import com.evtnet.evtnetback.error.HttpErrorException;
// import com.evtnet.evtnetback.mapper.EventoSearchMapper;
// import jakarta.transaction.Transactional;
// import org.springframework.data.domain.Sort;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Service;
//
// import java.time.LocalDateTime;
// import java.util.ArrayList;
// import java.util.List;
//
// @Service
// public class EventoServiceImpl extends BaseServiceImpl<Evento, Long> implements EventoService {
//
//     private final EventoRepository eventoRepo;
//     private final DisciplinaEventoRepository disciplinaEventoRepo;
//     private final DisciplinaRepository disciplinaBaseRepo;
//     private final ModoEventoRepository modoRepo;
//     private final TipoInscripcionEventoRepository tipoInscripcionRepo;
//     private final EspacioRepository espacioRepo;
//     private final EventoModoEventoRepository eventoModoEventoRepo;
//     private final InscripcionRepository inscripcionRepo;
//     private final AdministradorEventoRepository administradorEventoRepo;
//
//     public EventoServiceImpl(
//             EventoRepository eventoRepo,
//             DisciplinaEventoRepository disciplinaEventoRepo,
//             DisciplinaRepository disciplinaBaseRepo,
//             ModoEventoRepository modoRepo,
//             TipoInscripcionEventoRepository tipoInscripcionRepo,
//             EspacioRepository espacioRepo,
//             EventoModoEventoRepository eventoModoEventoRepo,
//             InscripcionRepository inscripcionRepo,
//             AdministradorEventoRepository administradorEventoRepo
//     ) {
//         super(eventoRepo);
//         this.eventoRepo = eventoRepo;
//         this.disciplinaEventoRepo = disciplinaEventoRepo;
//         this.disciplinaBaseRepo = disciplinaBaseRepo;
//         this.modoRepo = modoRepo;
//         this.tipoInscripcionRepo = tipoInscripcionRepo;
//         this.espacioRepo = espacioRepo;
//         this.eventoModoEventoRepo = eventoModoEventoRepo;
//         this.inscripcionRepo = inscripcionRepo;
//         this.administradorEventoRepo = administradorEventoRepo;
//     }
//
//     @Override
//     @Transactional
//     public List<DTOResultadoBusquedaEventos> buscar(DTOBusquedaEventos filtro) {
//         return eventoRepo.findAll(EventoSpecs.byFiltroBusqueda(filtro), Sort.by("fechaHoraInicio").ascending())
//                 .stream().map(EventoSearchMapper::toResultadoBusqueda).toList();
//     }
//
//     @Override
//     @Transactional
//     public List<DTOResultadoBusquedaMisEventos> buscarMisEventos(DTOBusquedaMisEventos filtro) {
//         return eventoRepo.findAll(EventoSpecs.byFiltroMisEventos(filtro), Sort.by("fechaHoraInicio").descending())
//                 .stream().map(EventoSearchMapper::toResultadoBusquedaMis).toList();
//     }
//
//     @Override
//     @Transactional
//     public DTOEvento obtenerEventoDetalle(long idEvento) {
//         Evento e = eventoRepo.findByIdFetchAll(idEvento)
//                 .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));
//
//         String username = null;
//         try { username = SecurityContextHolder.getContext().getAuthentication().getName(); }
//         catch (Exception ignored) {}
//
//         boolean inscripto = (username != null) &&
//                 inscripcionRepo.countByEventoIdAndUsuarioUsername(e.getId(), username) > 0;
//
//         boolean administrador = false;
//         if (username != null) {
//             try {
//                 administrador = eventoRepo.existsByEventoIdAndAdministradorUsername(e.getId(), username);
//             } catch (Exception ignored) { administrador = false; }
//         }
//
//         return EventoSearchMapper.toDTOEvento(e, inscripto, administrador);
//     }
//
//     @Override
//     @Transactional
//     public DTODatosCreacionEvento obtenerDatosCreacionEvento(Long idEspacioOrNull) {
//         String nombreEspacio = null;
//         if (idEspacioOrNull != null) {
//             Espacio esp = espacioRepo.findById(idEspacioOrNull)
//                     .orElseThrow(() -> new HttpErrorException(404, "Espacio no encontrado"));
//             nombreEspacio = esp.getNombre();
//         }
//
//         var tipos = tipoInscripcionRepo.findAll().stream()
//                 .map(t -> new DTODatosCreacionEvento.TipoInscripcion(t.getId(), t.getNombre()))
//                 .toList();
//
//         double comision = 0.12;
//         int diasHaciaAdelante = 30;
//
//         return new DTODatosCreacionEvento(nombreEspacio, tipos, comision, null, null, diasHaciaAdelante);
//     }
//
//     @Override
//     @Transactional
//     public long crearEvento(DTOEventoCreate r) {
//         // implementación comentada
//         return 0L;
//     }
//
//     @Override
//     @Transactional
//     public int obtenerCantidadEventosSuperpuestos(long idEspacio, long fechaDesdeMillis, long fechaHastaMillis) {
//         LocalDateTime desde = LocalDateTime.ofEpochSecond(fechaDesdeMillis / 1000, 0, java.time.ZoneOffset.UTC);
//         LocalDateTime hasta = LocalDateTime.ofEpochSecond(fechaHastaMillis / 1000, 0, java.time.ZoneOffset.UTC);
//         return eventoRepo.contarSuperpuestosPorEspacio(idEspacio, desde, hasta);
//     }
// }
