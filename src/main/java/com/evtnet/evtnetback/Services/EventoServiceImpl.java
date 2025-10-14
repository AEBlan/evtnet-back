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
import com.evtnet.evtnetback.util.GeographyUtil;
import com.evtnet.evtnetback.util.MercadoPagoSingleton;

import com.evtnet.evtnetback.util.RegistroSingleton;
import jakarta.persistence.TypedQuery;
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


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


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
	private final HorarioEspacioRepository horarioRepo;
	private final ChatRepository chatRepo;

	private final RegistroSingleton registroSingleton;

	@PersistenceContext
    private EntityManager entityManager;

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
	    ComisionPorInscripcionService comisionPorInscripcionService,
		HorarioEspacioRepository horarioRepo,
		ChatRepository chatRepo,
		RegistroSingleton registroSingleton
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
	this.horarioRepo = horarioRepo;
	this.chatRepo = chatRepo;
	this.registroSingleton = registroSingleton;

    }
     
@Override
@Transactional
public List<DTOResultadoBusquedaEventos> buscar(DTOBusquedaEventos filtro) throws Exception {

    // Parámetros globales ====
    double c_u = parametroSistemaService.getDecimal("c_u", new BigDecimal("0.4")).doubleValue();
    double c_d = parametroSistemaService.getDecimal("c_d", new BigDecimal("0.35")).doubleValue();
    double c_p = parametroSistemaService.getDecimal("c_p", new BigDecimal("0.25")).doubleValue();
    BigDecimal max_p = parametroSistemaService.getDecimal("max_p", new BigDecimal("20000"));
    BigDecimal max_d = parametroSistemaService.getDecimal("max_d", new BigDecimal("1000"));
    BigDecimal comision_inscripcion = parametroSistemaService.getDecimal("comision_inscripcion", new BigDecimal("0.1"));
    Integer ventana_de_eventos = parametroSistemaService.getInt("ventana_de_eventos", 20);

    if (filtro.ubicacion() != null && filtro.ubicacion().rango() != null) {
	max_d = new BigDecimal(filtro.ubicacion().rango());
    }


	// Excluir resultados irrelevantes

    List<String> keywords = Arrays.asList(filtro.texto().split("\s"))
	.stream().filter(k -> k.length() > 2).toList();

	String jpqlEventos = """
		SELECT DISTINCT e 
		FROM Evento e 
			JOIN e.eventosEstado ee 
			JOIN ee.estadoEvento est 
			JOIN e.subEspacio s 
			JOIN s.espacio esp 
			JOIN esp.tipoEspacio tesp 
			JOIN e.disciplinasEvento d
		WHERE ee.fechaHoraBaja is null 
			AND est.nombre LIKE 'Aceptado'
		""";
	String jpqlSuperEventos = """
		SELECT DISTINCT s 
		FROM SuperEvento s 
			JOIN s.eventos e 
			JOIN e.eventosEstado ee 
			JOIN ee.estadoEvento est 
			JOIN e.disciplinasEvento d 
		WHERE s.fechaHoraBaja is null
			AND ee.fechaHoraBaja is null
			AND est.nombre LIKE 'Aceptado'
		""";

	for (int i = 0; i < keywords.size(); i++) {
		jpqlEventos += " AND (" + 
		"LOWER (TRIM(e.nombre)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%')) OR " + 
		"LOWER (TRIM(e.descripcion)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%'))" + 
		"LOWER (TRIM(esp.nombre)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%'))" + 
		"LOWER (TRIM(esp.descripcion)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%'))" + 
		"LOWER (TRIM(esp.direccion)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%'))" + 
		")";

		jpqlSuperEventos += " AND (" + 
		"LOWER (TRIM(s.nombre)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%')) OR " + 
		"LOWER (TRIM(s.descripcion)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%'))" +
		")";
	}

	if (filtro.fechaDesde() != null) {
		jpqlEventos += " AND e.fechaHoraInicio >= :fechaDesde";
		jpqlSuperEventos += " AND (SELECT MIN(e.fechaHoraInicio) FROM Evento e WHERE e.superEvento = s) >= :fechaDesde";
	}

	if (filtro.fechaHasta() != null) {
		jpqlEventos += " AND e.fechaHoraFin <= :fechaHasta";
		jpqlSuperEventos += " AND (SELECT MAX(e.fechaHoraFin) FROM Evento e WHERE e.superEvento = s) >= :fechaHasta";
	}

	if (filtro.horaDesde() != null && filtro.horaHasta() != null) {
		jpqlEventos += """
			 AND CASE 
				WHEN FUNCTION('TIME', :horaDesde) <= FUNCTION('TIME', :horaHasta) THEN
					CASE WHEN (
						FUNCTION('TIME', e.fechaHoraInicio) >= FUNCTION('TIME', :horaDesde) AND 
						FUNCTION('TIME', e.fechaHoraFin) >= FUNCTION('TIME', :horaDesde) AND 
						FUNCTION('TIME', e.fechaHoraInicio) <= FUNCTION('TIME', :horaHasta) AND
						FUNCTION('TIME', e.fechaHoraFin) <= FUNCTION('TIME', :horaHasta)
					) THEN true ELSE false END
				ELSE
					CASE WHEN (
						(FUNCTION('TIME', e.fechaHoraInicio) >= FUNCTION('TIME', :horaDesde) AND 
						FUNCTION('TIME', e.fechaHoraFin) >= FUNCTION('TIME', :horaDesde)) OR 
						(FUNCTION('TIME', e.fechaHoraInicio) <= FUNCTION('TIME', :horaHasta) AND
						FUNCTION('TIME', e.fechaHoraFin) <= FUNCTION('TIME', :horaHasta))
					) THEN true ELSE false END
			END
			""";
	}

	if (filtro.tiposEspacio() != null && filtro.tiposEspacio().size() > 0) {
		jpqlEventos += " AND tesp.id IN :tiposEspacio";
	}

	if (filtro.disciplinas() != null && filtro.disciplinas().size() > 0) {
		jpqlEventos += " AND d.id IN :disciplinas";
		jpqlSuperEventos += " AND d.id IN :disciplinas";
	}

	if (filtro.precioLimite() != null) {
		jpqlEventos += " AND (e.precioInscripcion + e.adicionalPorInscripcion) * (1.0 + :comisionInscripcion) <= :precioLimite";
	}



	TypedQuery<Evento> queryEventos = entityManager.createQuery(jpqlEventos, Evento.class);
	TypedQuery<SuperEvento> querySuperEventos = entityManager.createQuery(jpqlSuperEventos, SuperEvento.class);

	for (int i = 0; i < keywords.size(); i++) {
		queryEventos.setParameter("kw" + i, keywords.get(i));
		querySuperEventos.setParameter("kw" + i, keywords.get(i));
	}

	if (filtro.fechaDesde() != null) {
		queryEventos.setParameter("fechaDesde", TimeUtil.fromMillis(filtro.fechaDesde()));
		querySuperEventos.setParameter("fechaDesde", TimeUtil.fromMillis(filtro.fechaDesde()));
	}

	if (filtro.fechaHasta() != null) {
		queryEventos.setParameter("fechaHasta", TimeUtil.fromMillis(filtro.fechaHasta()));
		querySuperEventos.setParameter("fechaHasta", TimeUtil.fromMillis(filtro.fechaHasta()));
	}

	if (filtro.horaDesde() != null && filtro.horaHasta() != null) {
		queryEventos.setParameter("horaDesde", TimeUtil.fromMillis(filtro.horaDesde()));
		queryEventos.setParameter("horaHasta", TimeUtil.fromMillis(filtro.horaHasta()));
	}

	if (filtro.tiposEspacio() != null && !filtro.tiposEspacio().isEmpty()) {
		queryEventos.setParameter("tiposEspacio", filtro.tiposEspacio());
	}

	if (filtro.disciplinas() != null && filtro.disciplinas().size() > 0) {
		queryEventos.setParameter("disciplinas", filtro.disciplinas());
		querySuperEventos.setParameter("disciplinas", filtro.disciplinas());
	}

	if (filtro.precioLimite() != null) {
		queryEventos.setParameter("precioLimite", filtro.precioLimite());
		queryEventos.setParameter("comisionInscripcion", comision_inscripcion);
	}

	List<Evento> eventos = new ArrayList<>();

	if (filtro.buscarEventos()) {
		eventos = queryEventos.getResultList();
	}
	
	List<SuperEvento> supereventos = new ArrayList<>();

	if (filtro.buscarSupereventos()) {
		supereventos = querySuperEventos.getResultList();
	}

	// Generar resultados ordenados

	String jpqlEventosRecientes = """
		SELECT e
		FROM Evento e
			JOIN e.inscripciones i
			JOIN i.usuario u
			JOIN e.eventosEstado ee 
			JOIN ee.estadoEvento est 
		WHERE 
			i.fechaHoraBaja is null
			AND u.username = :username
			AND ee.fechaHoraBaja is null 
			AND est.nombre LIKE 'Aceptado'
			AND e.fechaHoraInicio < CURRENT_TIMESTAMP
		ORDER BY e.fechaHoraInicio DESC
		""";
		
	TypedQuery<Evento> queryEventosRecientes = entityManager.createQuery(jpqlEventosRecientes, Evento.class);

	String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("No se encontró al usuario"));

	queryEventosRecientes.setParameter("username", username);
	queryEventosRecientes.setMaxResults(ventana_de_eventos);

	List<Evento> eventosRecientes = queryEventosRecientes.getResultList();


	Double latitud = null;
	Double longitud = null;

	if (filtro.ubicacion() != null) {
		latitud = filtro.ubicacion().latitud();
		longitud = filtro.ubicacion().longitud();
	}

	boolean ignoreLocation = false;

	if (latitud == null || longitud == null) {

		List<GeographyUtil.Location> locations = eventosRecientes.stream()
			.map(e ->
					new GeographyUtil.Location(
							e.getSubEspacio().getEspacio().getLatitudUbicacion().doubleValue(),
							e.getSubEspacio().getEspacio().getLongitudUbicacion().doubleValue()
					)
			).toList();

		if (!locations.isEmpty()) {
			GeographyUtil.Location centro = GeographyUtil.calculateCenter(locations);

			latitud = centro.getLatitude();
			longitud = centro.getLongitude();
		} else {
			ignoreLocation = true;
		}
	}

	GeographyUtil.Location centro = new GeographyUtil.Location(0, 0);

	if (!ignoreLocation) {
		centro = new GeographyUtil.Location(latitud, longitud);
	}

	double c_e = (double) eventosRecientes.stream().filter(er -> er.getSuperEvento() != null).count() / ventana_de_eventos;

	//Para que no queden todos los supereventos con puntuación 0
	if (c_e == 0.0) c_e = 0.05;

	List<DTOResultadoBusquedaEventos> resultados = new ArrayList<>();

	for (Evento e : eventos) {

		double u = 0;

		if(!ignoreLocation) {

			double distancia = GeographyUtil.calculateDistance(
					centro,
					new GeographyUtil.Location(
							e.getSubEspacio().getEspacio().getLatitudUbicacion().doubleValue(),
							e.getSubEspacio().getEspacio().getLongitudUbicacion().doubleValue()
				)) * 1000;

			if (distancia > max_d.doubleValue()) continue;
			u = Math.max(Math.log(max_d.doubleValue()/Math.max(distancia, 1)) / Math.log(max_d.doubleValue()), 0);

		}


		List<Long> disciplinasEvento = new ArrayList<>(e.getDisciplinasEvento().stream().filter(d -> d.getFechaHoraBaja() == null).map(d -> d.getDisciplina().getId()).toList());

		for (Long d : filtro.disciplinas()) {
			if (!disciplinasEvento.contains(d)) {
				disciplinasEvento.add(d);
			}
		}

		double d = (double) filtro.disciplinas().size() / (disciplinasEvento.size());

		Double precioLimite = filtro.precioLimite();

		if (precioLimite == null) {
			precioLimite = max_p.doubleValue();
		}

		double precio = e.getPrecioInscripcion().add(e.getAdicionalPorInscripcion()).multiply(comision_inscripcion.add(new BigDecimal(1.0))).doubleValue();

		double p = Math.max((precioLimite - precio)/precioLimite, 0);

		double puntuacion = (1 - c_e) * (c_u * u + c_d * d + c_p * p);

		resultados.add(DTOResultadoBusquedaEventos.builder()
			.esSuperevento(false)
			.id(e.getId())
			.nombre(e.getNombre())
			.fechaHoraInicio(TimeUtil.toMillis(e.getFechaHoraInicio()))
			.precio(precio)
			.nombreEspacio(e.getSubEspacio().getEspacio().getNombre())
			.disciplinas(e.getDisciplinasEvento().stream().map(di -> di.getDisciplina().getNombre()).toList())
			.fechaHoraProximoEvento(null)
			.puntuacion(puntuacion)
			.build());
	}


	for (SuperEvento s : supereventos) {
		List<Evento> eventosVigentes = s.getEventos().stream()
			.filter(
				e -> {
					List<EventoEstado> ees = e.getEventosEstado().stream()
						.filter(ee -> ee.getFechaHoraBaja() == null).toList();

					if (ees.size() != 1) return false;

					EventoEstado ee = ees.get(0);

					if (!ee.getEstadoEvento().getNombre().equals("Aceptado")) {
						return false;
					}

					return true;
				}).toList();

		List<Disciplina> disciplinasSuperEvento = eventosVigentes.stream()
			.map(
				e -> e.getDisciplinasEvento().stream()
					.filter(d -> d.getFechaHoraBaja() == null)
					.map(d -> d.getDisciplina()).toList()
			).flatMap(List::stream).toList();

		List<Long> disciplinasSuperEventoId = disciplinasSuperEvento.stream().map(d -> d.getId()).toList();

		for (Long d : filtro.disciplinas()) {
			if (!disciplinasSuperEventoId.contains(d)) {
				disciplinasSuperEventoId.add(d);
			}
		}

		double d = (double) filtro.disciplinas().size() / (disciplinasSuperEvento.size());

		double puntuacion = c_e * d;

		Evento proxEvento = null;

		for (Evento e : eventosVigentes) {
			if (e.getFechaHoraInicio().isBefore(java.time.LocalDateTime.now())) continue;

			if (proxEvento == null) {
				proxEvento = e;
				continue;
			}

			if (e.getFechaHoraInicio().isBefore(proxEvento.getFechaHoraInicio())) {
				proxEvento = e;
			}
		}

		Long fechaHoraProximoEvento = proxEvento == null ? null : TimeUtil.toMillis(proxEvento.getFechaHoraInicio());

		resultados.add(DTOResultadoBusquedaEventos.builder()
			.esSuperevento(true)
			.id(s.getId())
			.nombre(s.getNombre())
			.fechaHoraInicio(null)
			.precio(null)
			.nombreEspacio(null)
			.disciplinas(disciplinasSuperEvento.stream().map(ds -> ds.getNombre()).toList())
			.fechaHoraProximoEvento(fechaHoraProximoEvento)
			.puntuacion(puntuacion)
			.build());
	}


	resultados.sort((lhs, rhs) -> lhs.puntuacion() < rhs.puntuacion() ? 1 : -1);

	return resultados;
}   
    
	//TODO: Revisar
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
    public DTOEventoDetalle obtenerEventoDetalle(long idEvento) throws Exception {
		Evento e = eventoRepo.findByIdForDetalle(idEvento)
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe autenticarse para ver este evento"));

		EventoEstado ee = e.getEventosEstado().stream().filter(eei -> eei.getFechaHoraBaja() == null).toList().get(0);

		String estado = ee.getEstadoEvento().getNombre();
		String motivoCancelacion = Objects.equals(estado, "Cancelado") ? ee.getDescripcion() : null;

		List<AdministradorEvento> admins = e.getAdministradoresEvento().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername() == username).toList();

		boolean administrador = false;
		boolean organizador = false;

		if (admins.size() == 1) {
			String tipo = admins.get(0).getTipoAdministradorEvento().getNombre();
			if (tipo.equals("Administrador")) {
				administrador = true;
			}
			if (tipo.equals("Organizador")) {
				administrador = true;
				organizador = true;
			}
		}

		// TODO: Capaz habría que ver si es encargado del subespacio

		if (!administrador && !organizador) {
			if (estado.equalsIgnoreCase("En Revisión") || estado.equalsIgnoreCase("Rechazado")) {
				throw new Exception("Evento no encontrado");
			}
		}
    
	List<Inscripcion> inscripcionesActivas = inscripcionRepo.findActivasByEventoId(e.getId());
	e.setInscripciones(inscripcionesActivas);

	boolean inscripto = e.getInscripciones().stream().filter(i -> i.getUsuario().getNombre() == username).count() == 1;

	int participantes = inscripcionRepo.countParticipantesEfectivos(e.getId());
	boolean cupoLleno = e.getCantidadMaximaParticipantes() != null &&
		participantes >= e.getCantidadMaximaParticipantes();

	BigDecimal comision_inscripcion = parametroSistemaService.getDecimal("comision_inscripcion", new BigDecimal("0.1"));


	double precioBase = e.getPrecioInscripcion().doubleValue() + e.getAdicionalPorInscripcion().doubleValue();
	double precioTotal = precioBase * (1 + comision_inscripcion.doubleValue());

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
			    i.getUsuario().getApellido()
		    ))
		    .toList();

		DTOEventoDetalle.SuperEvento superevento = null;

		if (e.getSuperEvento() != null) {
			superevento = new DTOEventoDetalle.SuperEvento(
				e.getSuperEvento().getId(),
				e.getSuperEvento().getNombre()
			);
		}
    
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
				estado,
		motivoCancelacion,
		cupoLleno,
				superevento,
				inscripto,
				inscriptos,
		administrador,
				organizador,
				e.getChat().getId()
	);
    }
    
    

    
    
    @Override
    @Transactional
    public DTODatosCreacionEvento obtenerDatosCreacionEvento(Long idEspacio) throws Exception {
		if (idEspacio == null)
			throw new HttpErrorException(400, "Debe indicar un espacio para crear el evento");
		
		Espacio espacio = espacioRepo.findById(idEspacio)
			.orElseThrow(() -> new HttpErrorException(404, "Espacio no encontrado"));
		
		BigDecimal comision_inscripcion = parametroSistemaService.getDecimal("comision_inscripcion", new BigDecimal("0.1"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe autenticarse para ver este evento"));

		boolean administrador = espacio.getAdministradoresEspacio().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(username)).count() == 1;

		return new DTODatosCreacionEvento(
			espacio.getNombre(),
			espacio.getSubEspacios().stream().map(s -> {

				List<ConfiguracionHorarioEspacio> cronogramas = new ArrayList<>(s.getConfiguracionesHorarioEspacio().stream().filter(c -> c.getFechaHasta().isAfter(LocalDateTime.now())).toList());

				cronogramas.sort((lhs, rhs) -> lhs.getFechaDesde().isBefore(rhs.getFechaDesde()) ? -1 : 1);
				
				long diasHaciaAdelante = 0;

				for (ConfiguracionHorarioEspacio c : cronogramas) {
					LocalDateTime a = LocalDateTime.now().plusDays(c.getDiasAntelacion());

					// Si los días de antelación no alcanzan el comienzo del cronograma, quedarse con los anteriores
					if (a.isBefore(c.getFechaDesde())) {
						break;
					}

					// Si alcanzaron el cronograma, quedarse con los días de antelación
					diasHaciaAdelante = c.getDiasAntelacion();

					// Si no llegan al final del cronograma, finalizar bucle
					if (a.isBefore(c.getFechaHasta())) {
						break;
					}

					// Si superan el final del cronograma, analizar el siguiente
				}

				return new DTODatosCreacionEvento.SubEspacio(
					s.getId(), 
					s.getNombre(),
					diasHaciaAdelante,
					s.getCapacidadmaxima()
				);
			}).toList(),
			espacio.getRequiereAprobarEventos(),
			comision_inscripcion.doubleValue(),
			espacio.getTipoEspacio().getNombre().equalsIgnoreCase("Público"),
			administrador
		);
    }

	private void validarDatosCreacionEvento(DTOEventoCreate req) throws Exception {
		if (req.getNombre().length() < 1 || req.getNombre().length() > 50) {
			throw new Exception("El nombre debe tener entre 1 y 50 caracteres");
		}
		if (req.getDescripcion().length() > 50) {
			throw new Exception("La descripción no debe superar los 500 caracteres");
		}

		SubEspacio subespacio = subEspacioRepo.findById(req.getSubEspacioId()).orElseThrow(() -> new Exception("No se encontró el subespacio"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe autenticarse para ver este evento"));

		boolean administrador = subespacio.getEspacio().getAdministradoresEspacio().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(username)).count() == 1;

		boolean publico = subespacio.getEspacio().getTipoEspacio().getNombre().equalsIgnoreCase("Público");

		if (!administrador && !req.isUsarCronograma() && !publico) {
			throw new Exception("Solo los administradores del espacio pueden organizar eventos de forma libre");
		}

		if (!(req.getFechaHoraInicio().isBefore(req.getFechaHoraFin()) && req.getFechaHoraInicio().isAfter(LocalDateTime.now()))) {
			throw new Exception("Fechas de inicio y fin inconsistentes. Asegúrese de que no hayan pasado.");
		}

		if (!req.isUsarCronograma()) {
			ConfiguracionHorarioEspacio config = null;
			for (ConfiguracionHorarioEspacio c : subespacio.getConfiguracionesHorarioEspacio()) {
				if (c.getFechaDesde().isBefore(req.getFechaHoraInicio()) && c.getFechaHasta().isAfter(req.getFechaHoraFin())) {
					config = c;
					break;
				}
			}

			if (config != null) {
				boolean ok = false;
				for (ExcepcionHorarioEspacio e : config.getExcepcionesHorarioEspacio()) {
					if (e.getFechaHoraDesde().isBefore(req.getFechaHoraInicio()) && e.getFechaHoraHasta().isAfter(req.getFechaHoraFin())) {
						ok = true;
						break;
					}
				}

				if (!ok) {
					throw new Exception("Hay un cronograma vigente en las fechas seleccionadas, y no hay excepciones registradas");
				}
			}
		}

		if (req.isUsarCronograma()) {
			HorarioEspacio h = horarioRepo.findById(req.getHorarioId()).orElseThrow(() -> new Exception("No se encontró el horario"));

			if (h.getConfiguracionHorarioEspacio().getSubEspacio() != subespacio) {
				throw new Exception("El horario seleccionado no corresponde al subespacio");
			}

			if (h.getConfiguracionHorarioEspacio().getFechaHasta().isBefore(LocalDateTime.now())) {
				throw new Exception("Este horario ya no está disponible");
			}

			if (req.getFechaHoraInicio().toLocalTime().compareTo(h.getHoraDesde()) != 0) {
				throw new Exception("La hora de inicio indicada no coincide con la del horario");
			}

			if (req.getFechaHoraFin().toLocalTime().compareTo(h.getHoraHasta()) != 0) {
				throw new Exception("La hora de finalización indicada no coincide con la del horario");
			}

			if (req.getFechaHoraInicio().getDayOfWeek().getValue() != getDiaSemana(h.getDiaSemana())) {
				throw new Exception("El día seleccionado no corresponde al horario indicado");
			}

			List<ConfiguracionHorarioEspacio> cronogramas = new ArrayList<>(subespacio.getConfiguracionesHorarioEspacio().stream().filter(c -> c.getFechaHasta().isAfter(LocalDateTime.now())).toList());

			cronogramas.sort((lhs, rhs) -> lhs.getFechaDesde().isBefore(rhs.getFechaDesde()) ? -1 : 1);

			long diasHaciaAdelante = 0;

			for (ConfiguracionHorarioEspacio c : cronogramas) {
				LocalDateTime a = LocalDateTime.now().plusDays(c.getDiasAntelacion());

				// Si los días de antelación no alcanzan el comienzo del cronograma, quedarse con los anteriores
				if (a.isBefore(c.getFechaDesde())) {
					break;
				}

				// Si alcanzaron el cronograma, quedarse con los días de antelación
				diasHaciaAdelante = c.getDiasAntelacion();

				// Si no llegan al final del cronograma, finalizar bucle
				if (a.isBefore(c.getFechaHasta())) {
					break;
				}

				// Si superan el final del cronograma, analizar el siguiente
			}

			if (!LocalDateTime.now().plusDays(diasHaciaAdelante).isAfter(req.getFechaHoraInicio())) {
				throw new Exception("No puede organizar el evento con tanta antelación");
			}
		}

		for (Long d : req.getDisciplinas()) {
			if (!subespacio.getDisciplinasSubespacio().stream().map(di -> di.getDisciplina().getId()).toList().contains(d)) {
				throw new Exception("Algunas disciplinas no son soportadas por este subespacio");
			}
		}

		if (req.getPrecio() < 0.0) {
			throw new Exception("El precio no puede ser negativo");
		}

		if (req.getMaxParticipantes() < 2) {
			throw new Exception("Debe haber al menos dos participantes");
		}

		if (req.getMaxParticipantes() > subespacio.getCapacidadmaxima()) {
			throw new Exception("La cantidad máxima de participantes supera la capacidad máxima del subespacio");
		}
	}

	@Override
    @Transactional
	public DTOPreferenciaPago pagarCreacionEvento(DTOEventoCreate req) throws Exception {
		validarDatosCreacionEvento(req);

		SubEspacio subespacio = subEspacioRepo.findById(req.getSubEspacioId()).orElseThrow(() -> new Exception("No se encontró el subespacio"));

		HorarioEspacio h = null;

		if (req.isUsarCronograma()) {
			h = horarioRepo.findById(req.getHorarioId()).orElseThrow(() -> new Exception("No se encontró el horario"));
		} else {
			throw new Exception("No debe pagar porque no está utilizando un cronograma");
		}

		BigDecimal comision_organizacion = parametroSistemaService.getDecimal("comision_organizacion", new BigDecimal("0.15"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe autenticarse para ver este evento"));
		List<String> admins = subespacio.getEspacio().getAdministradoresEspacio().stream().filter(a -> a.getFechaHoraBaja() == null).map(a -> a.getUsuario().getUsername()).toList();

		if (admins.contains(username)) {
			throw new Exception("No debe pagar porque usted es administrador del espacio");
		}

		Usuario propietario = subespacio.getEspacio().getAdministradoresEspacio().stream().filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorEspacio().getNombre().equals("Propietario")).toList().get(0).getUsuario();


		DTOPreferenciaPago pref = mercadoPagoSingleton.createPreference("Organización de evento '" + req.getNombre() + "'", h.getPrecioOrganizacion(), comision_organizacion, propietario, "/CrearEvento/" + subespacio.getEspacio().getId());

		return pref;
	}

    @Override
    @Transactional
    public long crearEvento(DTOEventoCreate r) throws Exception {
		validarDatosCreacionEvento(r);

		SubEspacio subespacio = subEspacioRepo.findById(r.getSubEspacioId()).orElseThrow(() -> new Exception("No se encontró el subespacio"));
		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe autenticarse para ver este evento"));
		List<String> admins = subespacio.getEspacio().getAdministradoresEspacio().stream().filter(a -> a.getFechaHoraBaja() == null).map(a -> a.getUsuario().getUsername()).toList();

		boolean pagoRealizado = false;

		if (r.isUsarCronograma() && !admins.contains(username)) {
			mercadoPagoSingleton.verifyPayments(List.of(r.getPago()));
			pagoRealizado = true;
		}

		HorarioEspacio h = null;

		if (r.isUsarCronograma()) {
			h = horarioRepo.findById(r.getHorarioId()).orElseThrow(() -> new Exception("No se encontró el horario"));
		}

		int cant_max_invitados_default = parametroSistemaService.getInt("cant_max_invitados_default", 5);

		List<Disciplina> disciplinas = disciplinaBaseRepo.findAllById(r.getDisciplinas());

		Usuario organizador = usuarioRepo.findByUsername(username).orElseThrow(() -> new Exception("Usuario inválido"));

		boolean publico = subespacio.getEspacio().getTipoEspacio().getNombre().equalsIgnoreCase("Público");


		// Crear el evento
		Evento e = new Evento();
		e.setNombre(r.getNombre());
		e.setDescripcion(r.getDescripcion());
		e.setFechaHoraInicio(r.getFechaHoraInicio());
		e.setFechaHoraFin(r.getFechaHoraFin());
		e.setPrecioInscripcion(new BigDecimal(r.getPrecio()));
		e.setCantidadMaximaInvitados(cant_max_invitados_default);
		e.setCantidadMaximaParticipantes(r.getMaxParticipantes());
		e.setPrecioOrganizacion(h != null ? h.getPrecioOrganizacion() : new BigDecimal(0.0d));
		e.setAdicionalPorInscripcion(h != null ? h.getAdicionalPorInscripcion() : new BigDecimal(0.0));
		e.setSubEspacio(subespacio);

		e = eventoRepo.save(e);

		for (Disciplina d : disciplinas) {
			DisciplinaEvento de = DisciplinaEvento.builder()
					.fechaHoraAlta(LocalDateTime.now())
					.evento(e)
					.disciplina(d)
					.build();

			disciplinaEventoRepo.save(de);
		}

		if (!publico) {
			TipoAdministradorEvento tipoOrg = tipoAdminEventoRepo.findByNombreIgnoreCase("Organizador")
					.orElseThrow(() -> new HttpErrorException(500, "TipoAdministradorEvento 'Organizador' no encontrado"));

			AdministradorEvento ae = AdministradorEvento.builder()
					.evento(e)
					.fechaHoraAlta(LocalDateTime.now())
					.usuario(organizador)
					.tipoAdministradorEvento(tipoOrg)
					.build();

			administradorEventoRepo.save(ae);
		}

		EstadoEvento estado = null;
		if (subespacio.getEspacio().getRequiereAprobarEventos()) {
			estado = estadoEventoRepo.findByNombreIgnoreCase("En Revisión").orElseThrow(() -> new Exception("No se encontró el estado inicial requerido"));
		} else {
			estado = estadoEventoRepo.findByNombreIgnoreCase("Aceptado").orElseThrow(() -> new Exception("No se encontró el estado inicial requerido"));
		}

		EventoEstado ee = EventoEstado.builder()
				.evento(e)
				.estadoEvento(estado)
				.fechaHoraAlta(LocalDateTime.now())
				.build();

		eventoEstadoRepo.save(ee);

		if (!publico) {
			// Crear Chat asociado
			Chat chat = Chat.builder()
					.tipo(Chat.Tipo.EVENTO)
					.fechaHoraAlta(LocalDateTime.now())
					.evento(e)
					.build();

			chatRepo.save(chat);
		}


		registroSingleton.write("Eventos", "evento", "creacion", "Evento de ID " + e.getId() + " nombre '" + e.getNombre() + "'");

		if (pagoRealizado) {
			registroSingleton.write("Pagos", "pago", "ejecucion", "Por organización de evento de ID " + e.getId() + " nombre '" + e.getNombre() + "'");
			registroSingleton.write("Pagos", "cobro_comision", "ejecucion", "Por organización de evento de ID " + e.getId() + " nombre '" + e.getNombre() + "'");
		}


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




	private int getDiaSemana(String dia) throws Exception {
		switch (dia) {
			case "Lunes":
				return 1;
			case "Martes":
				return 2;
			case "Miércoles":
				return 3;
			case "Jueves":
				return 4;
			case "Viernes":
				return 5;
			case "Sábado":
				return 6;
			case "Domingo":
				return 7;
			default:
				throw new Exception("Día no válido en el horario");
		}
	}
}
