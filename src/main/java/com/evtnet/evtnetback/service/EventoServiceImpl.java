package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.dto.supereventos.DTOAdministradoresSuperevento;
import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.repository.*;
import com.evtnet.evtnetback.repository.specs.EventoSpecs;
import com.evtnet.evtnetback.dto.eventos.*;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;
import com.evtnet.evtnetback.dto.usuarios.DTOPago;
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
import com.evtnet.evtnetback.repository.specs.DenunciaEventoSpecs;

import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import com.evtnet.evtnetback.util.TimeUtil;


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
    private final AdministradorSuperEventoRepository administradorSuperEventoRepo;
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
    private final TipoAdministradorSuperEventoRepository tipoAdminSuperEventoRepo;
    private static final ZoneId ZONA_ARG = ZoneId.of("America/Argentina/Buenos_Aires");
    private final SubEspacioRepository subEspacioRepo;
    private final DisciplinaSubEspacioRepository disciplinaSubEspacioRepo;
    private final EstadoEventoRepository estadoEventoRepo;
    private final EventoEstadoRepository eventoEstadoRepo;
    private final ParametroSistemaService parametroSistemaService;
    private final ComisionPorInscripcionService comisionPorInscripcionService;
	private final ComisionPorInscripcionRepository comisionPorInscripcionRepo;
	private final ComisionPorOrganizacionRepository comisionPorOrganizacionRepo;
	private final HorarioEspacioRepository horarioRepo;
	private final ChatRepository chatRepo;
	private final PorcentajeReintegroCancelacionInscripcionRepository porcentajeReintegroCancelacionInscripcionRepo;
	private final TipoAdministradorEventoRepository tipoAdminRepo;
	private final RegistroSingleton registroSingleton;

	private final MailService mailService;

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
		AdministradorSuperEventoRepository administradorSuperEventoRepo,
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
		TipoAdministradorSuperEventoRepository tipoAdminSuperEventoRepo,
	    SubEspacioRepository subEspacioRepo,
	    DisciplinaSubEspacioRepository disciplinaSubEspacioRepo,
	    EstadoEventoRepository estadoEventoRepo,
	    EventoEstadoRepository eventoEstadoRepo,
	    ParametroSistemaService parametroSistemaService,
	    ComisionPorInscripcionService comisionPorInscripcionService,
		ComisionPorInscripcionRepository comisionPorInscripcionRepo,
		ComisionPorOrganizacionRepository comisionPorOrganizacionRepo,
		HorarioEspacioRepository horarioRepo,
		ChatRepository chatRepo,
		PorcentajeReintegroCancelacionInscripcionRepository porcentajeReintegroCancelacionInscripcionRepo,
		TipoAdministradorEventoRepository tipoAdminRepo,
		RegistroSingleton registroSingleton,
		MailService mailService
    ) {
	super(eventoRepo);
	this.eventoRepo = eventoRepo;
	this.disciplinaEventoRepo = disciplinaEventoRepo;
	this.disciplinaBaseRepo = disciplinaBaseRepo;
	this.espacioRepo = espacioRepo;
	this.inscripcionRepo = inscripcionRepo;
	this.administradorEventoRepo = administradorEventoRepo;
	this.administradorSuperEventoRepo = administradorSuperEventoRepo;
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
	this.tipoAdminSuperEventoRepo = tipoAdminSuperEventoRepo;
	this.subEspacioRepo = subEspacioRepo;
	this.disciplinaSubEspacioRepo = disciplinaSubEspacioRepo;
	this.estadoEventoRepo = estadoEventoRepo;
	this.eventoEstadoRepo = eventoEstadoRepo;
	this.parametroSistemaService = parametroSistemaService;
	this.comisionPorInscripcionService = comisionPorInscripcionService;
	this.comisionPorInscripcionRepo = comisionPorInscripcionRepo;
	this.comisionPorOrganizacionRepo = comisionPorOrganizacionRepo;
	this.horarioRepo = horarioRepo;
	this.chatRepo = chatRepo;
	this.registroSingleton = registroSingleton;
	this.mailService = mailService;
	this.porcentajeReintegroCancelacionInscripcionRepo = porcentajeReintegroCancelacionInscripcionRepo;
	this.tipoAdminRepo = tipoAdminRepo;
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
		//BigDecimal comision_inscripcion = parametroSistemaService.getDecimal("comision_inscripcion", new BigDecimal("0.1"));
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
				LEFT JOIN e.disciplinasEvento de
				JOIN de.disciplina d
			WHERE ee.fechaHoraBaja is null 
				AND est.nombre LIKE 'Aceptado'
				AND de.fechaHoraBaja is null
			""";
		String jpqlSuperEventos = """
			SELECT DISTINCT s 
			FROM SuperEvento s 
				JOIN s.eventos e 
				JOIN e.eventosEstado ee 
				JOIN ee.estadoEvento est 
				LEFT JOIN e.disciplinasEvento de
				JOIN de.disciplina d
			WHERE s.fechaHoraBaja is null
				AND ee.fechaHoraBaja is null
				AND est.nombre LIKE 'Aceptado'
				AND de.fechaHoraBaja is null
			""";

		for (int i = 0; i < keywords.size(); i++) {
			jpqlEventos += " AND (" +
			"LOWER (TRIM(e.nombre)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%')) OR " +
			"LOWER (TRIM(e.descripcion)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%')) OR " +
			"LOWER (TRIM(esp.nombre)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%')) OR " +
			"LOWER (TRIM(esp.descripcion)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%')) OR " +
			"LOWER (TRIM(esp.direccionUbicacion)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%'))" +
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
			jpqlEventos += " AND (e.precioInscripcion + e.adicionalPorInscripcion) * (1.0 + COALESCE((SELECT c.porcentaje / 100.0 FROM ComisionPorInscripcion c WHERE c.montoLimite = (SELECT MAX(c2.montoLimite) FROM ComisionPorInscripcion c2 WHERE c2.montoLimite <= e.precioInscripcion + e.adicionalPorInscripcion AND c2.fechaDesde <= CURRENT_TIMESTAMP AND (c2.fechaHasta IS NULL OR c2.fechaHasta > CURRENT_TIMESTAMP)) AND c.fechaDesde <= CURRENT_TIMESTAMP AND (c.fechaHasta IS NULL OR c.fechaHasta > CURRENT_TIMESTAMP)), 0)) <= :precioLimite";
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
			//queryEventos.setParameter("comisionInscripcion", comision_inscripcion);
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

			BigDecimal precioTmp = e.getPrecioInscripcion().add(e.getAdicionalPorInscripcion());
			double precio = precioTmp.multiply(getComisionInscripcion(precioTmp).add(new BigDecimal(1.0))).doubleValue();

			double p = Math.max((precioLimite - precio)/precioLimite, 0);

			double puntuacion = (1 - c_e) * (c_u * u + c_d * d + c_p * p);

			resultados.add(DTOResultadoBusquedaEventos.builder()
				.esSuperevento(false)
				.id(e.getId())
				.nombre(e.getNombre())
				.fechaHoraInicio(TimeUtil.toMillis(e.getFechaHoraInicio()))
				.precio(precio)
				.nombreEspacio(e.getSubEspacio().getEspacio().getNombre())
				.disciplinas(e.getDisciplinasEvento().stream().filter(de -> de.getFechaHoraBaja() == null).map(di -> di.getDisciplina().getNombre()).toList())
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

    @Override
    @Transactional
    public List<DTOResultadoBusquedaMisEventos> buscarMisEventos(DTOBusquedaMisEventos filtro) throws Exception {
		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe iniciar sesión para ver sus eventos"));

		return eventoRepo.findAll(
				EventoSpecs.byFiltroMisEventos(filtro, username),
				Sort.by("fechaHoraInicio").ascending()
			)
			.stream()
			.map(e -> EventoSearchMapper.toResultadoBusquedaMis(e, username))
				.filter(e -> !e.rol().isEmpty())
				.filter(e -> {
					if (!filtro.organizador() && !filtro.administrador() && !filtro.encargado() && !filtro.participante()) {
						return true; // Si no hay filtros por roles, incluir todos
					}
					return (filtro.organizador() && e.rol().contains("Organizador"))
							|| (filtro.administrador() && e.rol().contains("Administrador"))
							|| (filtro.encargado() && e.rol().contains("Encargado"))
							|| (filtro.participante() && e.rol().contains("Participante"));
				})
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

		List<AdministradorEvento> admins = e.getAdministradoresEvento().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equalsIgnoreCase(username)).toList();

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

		if (!administrador && !organizador) {
			if (estado.equalsIgnoreCase("En Revisión") || estado.equalsIgnoreCase("Rechazado")) {
				throw new Exception("Evento no encontrado");
			}
		}
    
		List<Inscripcion> inscripcionesActivas = inscripcionRepo.findActivasByEventoId(e.getId());
		e.setInscripciones(inscripcionesActivas);

		boolean inscripto = e.getInscripciones().stream().filter(i -> i.getFechaHoraBaja() == null &&  i.getUsuario().getUsername().equals(username)).count() == 1;

		int participantes = inscripcionRepo.countParticipantesEfectivos(e.getId());
		boolean cupoLleno = e.getCantidadMaximaParticipantes() != null &&
		participantes >= e.getCantidadMaximaParticipantes();

		BigDecimal comision_inscripcion = parametroSistemaService.getDecimal("comision_inscripcion", BigDecimal.valueOf(0.1));

		BigDecimal precioBase = e.getPrecioInscripcion().add(e.getAdicionalPorInscripcion());
		double precioTotal = precioBase.multiply(getComisionInscripcion(precioBase).add(BigDecimal.valueOf(1.0))).doubleValue();

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
				.filter(de -> de.getDisciplina() != null && de.getFechaHoraBaja() == null)
				.map(de -> de.getDisciplina().getNombre())
				.filter(Objects::nonNull)
				.toList();

		List<DTOEventoDetalle.Inscripto> inscriptos = (e.getInscripciones() == null || (!inscripto && !administrador && !organizador))
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
			precioBase.doubleValue(),
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
		
		//BigDecimal comision_inscripcion = parametroSistemaService.getDecimal("comision_inscripcion", new BigDecimal("0.1"));

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
			comisionPorInscripcionRepo.findAll().stream().filter(c -> c.getFechaDesde().isBefore(LocalDateTime.now()) && (c.getFechaHasta() == null || c.getFechaHasta().isAfter(LocalDateTime.now()))).map(c -> new DTODatosCreacionEvento.Comision(c.getMontoLimite().doubleValue(), c.getPorcentaje().doubleValue()/100f)).toList(),
			espacio.getTipoEspacio().getNombre().equalsIgnoreCase("Público"),
			administrador,
			espacio.getBasesYCondiciones() != null && !espacio.getBasesYCondiciones().isEmpty()
		);
    }

	private void validarDatosCreacionEvento(DTOEventoCreate req) throws Exception {
		if (req.getNombre().length() < 1 || req.getNombre().length() > 50) {
			throw new Exception("El nombre debe tener entre 1 y 50 caracteres");
		}
		if (req.getDescripcion().length() > 500) {
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

		//BigDecimal comision_organizacion = parametroSistemaService.getDecimal("comision_organizacion", new BigDecimal("0.15"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe autenticarse para ver este evento"));
		List<String> admins = subespacio.getEspacio().getAdministradoresEspacio().stream().filter(a -> a.getFechaHoraBaja() == null).map(a -> a.getUsuario().getUsername()).toList();

		if (admins.contains(username)) {
			throw new Exception("No debe pagar porque usted es administrador del espacio");
		}

		Usuario propietario = subespacio.getEspacio().getAdministradoresEspacio().stream().filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorEspacio().getNombre().equals("Propietario")).toList().get(0).getUsuario();

		if (h.getPrecioOrganizacion().doubleValue() == 0f) {
			throw new HttpErrorException(900, "No es necesario realizar un pago");
		}

		BigDecimal comision_organizacion = getComisionOrganizacion(h.getPrecioOrganizacion());

		DTOPreferenciaPago pref = mercadoPagoSingleton.createPreference("Organización de evento '" + req.getNombre() + "'", h.getPrecioOrganizacion().multiply(comision_organizacion.add(BigDecimal.valueOf(1))), comision_organizacion, propietario, "/CrearEvento/" + subespacio.getEspacio().getId());

		return pref;
	}

    @Override
    @Transactional
    public long crearEvento(DTOEventoCreate r) throws Exception {
		validarDatosCreacionEvento(r);

		SubEspacio subespacio = subEspacioRepo.findById(r.getSubEspacioId()).orElseThrow(() -> new Exception("No se encontró el subespacio"));
		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe autenticarse para ver este evento"));
		List<String> admins = subespacio.getEspacio().getAdministradoresEspacio().stream().filter(a -> a.getFechaHoraBaja() == null).map(a -> a.getUsuario().getUsername()).toList();

		HorarioEspacio h = null;

		if (r.isUsarCronograma()) {
			h = horarioRepo.findById(r.getHorarioId()).orElseThrow(() -> new Exception("No se encontró el horario"));
		}

		boolean pagoRealizado = false;

		if (r.isUsarCronograma() && !admins.contains(username)) {
			if (h.getPrecioOrganizacion().doubleValue() > 0f) {
				mercadoPagoSingleton.verifyPayments(List.of(r.getPago()));
			}
			pagoRealizado = true;
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


		TipoAdministradorEvento tipoOrg = tipoAdminEventoRepo.findByNombreIgnoreCase("Organizador")
				.orElseThrow(() -> new HttpErrorException(500, "TipoAdministradorEvento 'Organizador' no encontrado"));

		AdministradorEvento ae = AdministradorEvento.builder()
				.evento(e)
				.fechaHoraAlta(LocalDateTime.now())
				.usuario(organizador)
				.tipoAdministradorEvento(tipoOrg)
				.build();

		administradorEventoRepo.save(ae);


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

		// Crear Chat asociado
		Chat chat = Chat.builder()
				.tipo(Chat.Tipo.EVENTO)
				.fechaHoraAlta(LocalDateTime.now())
				.evento(e)
				.build();

		chatRepo.save(chat);


		registroSingleton.write("Eventos", "evento", "creacion", "Evento de ID " + e.getId() + " nombre '" + e.getNombre() + "'");

		if (pagoRealizado) {
			registroSingleton.write("Pagos", "pago", "ejecucion", "Por organización de evento de ID " + e.getId() + " nombre '" + e.getNombre() + "'");
			registroSingleton.write("Pagos", "cobro_comision", "ejecucion", "Por organización de evento de ID " + e.getId() + " nombre '" + e.getNombre() + "'");
		}


		return e.getId();
    }

	//TODO: Revisar
    @Override
    @Transactional
    public int obtenerCantidadEventosSuperpuestos(long idEspacio, long fechaDesdeMillis, long fechaHastaMillis) {
		LocalDateTime desde = LocalDateTime.ofEpochSecond(fechaDesdeMillis / 1000, 0, java.time.ZoneOffset.UTC);
		LocalDateTime hasta = LocalDateTime.ofEpochSecond(fechaHastaMillis / 1000, 0, java.time.ZoneOffset.UTC);
		return eventoRepo.contarSuperpuestosPorEspacio(idEspacio, desde, hasta);
    }



    @Override @Transactional
    public DTOEventoParaInscripcion obtenerEventoParaInscripcion(long idEvento) throws Exception {
		// cambia a findByIdForDetalle
		Evento e = eventoRepo.findByIdForDetalle(idEvento)
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		String estado = e.getEventosEstado().stream().filter(ee -> ee.getFechaHoraBaja() == null).toList().get(0).getEstadoEvento().getNombre();

		if (!estado.equalsIgnoreCase("Aceptado")) {
			throw new Exception("No puede inscribirse al evento porque el mismo no está aceptado");
		}

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe autenticarse para ver este evento"));

		List<Inscripcion> inscripcion = e.getInscripciones().stream().filter(i -> i.getFechaHoraBaja() == null && i.getUsuario().getUsername().equals(username)).toList();

		if (!inscripcion.isEmpty()) {
			throw new Exception("Ya está inscripto a este evento");
		}

		DTOEventoParaInscripcion.Espacio espacio = null;

		espacio = DTOEventoParaInscripcion.Espacio.builder()
			.id(e.getSubEspacio().getEspacio().getId())
			.nombre(e.getSubEspacio().getEspacio().getNombre())
			.descripcion(e.getSubEspacio().getEspacio().getDescripcion())
			.build();

		//BigDecimal comision_inscripcion = parametroSistemaService.getDecimal("comision_inscripcion", new BigDecimal("0.1"));

		BigDecimal precioBase = e.getPrecioInscripcion().add(e.getAdicionalPorInscripcion());
		BigDecimal precio = precioBase.multiply(getComisionInscripcion(precioBase).add(BigDecimal.valueOf(1.0)));

		int cuposDisponibles = e.getCantidadMaximaParticipantes() - e.getInscripciones().stream().filter(i -> i.getFechaHoraBaja() == null).mapToInt(i -> 1 + i.getInvitados().size()).sum();


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
			.precioPorAsistente(precio)
			.cantidadMaximaInvitados(e.getCantidadMaximaInvitados())
			.limiteParticipantes(cuposDisponibles)
			.build();
    }

	@Override
	@Transactional
	public DTOVerificacionPrePago verificarDatosPrePago(DTOInscripcion dto) throws Exception {
		verificarDatosPrePagoBool(dto);

		ArrayList<DTOPreferenciaPago> prefs = new ArrayList<>();
		//BigDecimal comision_inscripcion = parametroSistemaService.getDecimal("comision_inscripcion", new BigDecimal("0.1"));

		Evento e = eventoRepo.findById(dto.getIdEvento())
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		String url = "/Evento/" + e.getId() + "/Inscribirme";

		Usuario organizador = e.getOrganizador();

		Usuario propietario = null;

		boolean publico = e.getSubEspacio().getEspacio().getTipoEspacio().getNombre().equalsIgnoreCase("Público");

		if (!publico) {

			List<Usuario> props = e.getSubEspacio().getEspacio().getAdministradoresEspacio().stream().filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorEspacio().getNombre().equalsIgnoreCase("Propietario")).map(a -> a.getUsuario()).toList();

			if (!props.isEmpty()) {
				propietario = props.get(0);
			}
		}

		if (e.getPrecioInscripcion().compareTo(BigDecimal.ZERO) != 0) {
			BigDecimal precioBase = e.getPrecioInscripcion().multiply(new BigDecimal(1 + dto.getInvitados().size()));
			prefs.add(mercadoPagoSingleton.createPreference("Inscripción a evento " + e.getNombre(), precioBase, getComisionInscripcion(precioBase), organizador, url));
		}

		if (!publico && e.getAdicionalPorInscripcion().compareTo(BigDecimal.ZERO) != 0) {
			BigDecimal precioBase = e.getAdicionalPorInscripcion().multiply(new BigDecimal(1 + dto.getInvitados().size()));
			prefs.add(mercadoPagoSingleton.createPreference("Adicional a espacio por inscripción a evento " + e.getNombre(), precioBase, getComisionInscripcion(precioBase), propietario, url));
		}


		return DTOVerificacionPrePago.builder()
			.valido(true)
			.preferencias(prefs)
			.build();
	}

	private void verificarDatosPrePagoBool(DTOInscripcion dto) throws Exception {
		Evento e = eventoRepo.findById(dto.getIdEvento())
				.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));
		
		String username = CurrentUser.getUsername().orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

		List<EventoEstado> ees = e.getEventosEstado().stream().filter(es -> es.getFechaHoraBaja() == null).toList();

		if (ees.size() != 1) {
			throw new Exception("El evento se encuentra en un estado no válido");
		}

		if (!ees.get(0).getEstadoEvento().getNombre().equalsIgnoreCase("Aceptado")) {
			throw new Exception("No puede inscribirse al evento, pues el mismo no se encuentra en estado 'Aceptado'");
		}

		// ya inscripto (solo si tiene una inscripción activa)
		if (inscripcionRepo.countActivasByEventoIdAndUsuarioUsername(e.getId(), username) > 0) 
			throw new Exception("Ya está inscripto a este evento");

		// Zona configurada en application.properties
		ZoneId zone = ZoneId.of(appTimezone);

		// ahora en zona ARG
		ZonedDateTime ahora = ZonedDateTime.now(zone);

		// inicio interpretado directamente en la zona configurada (sin forzar UTC)
		ZonedDateTime inicio = e.getFechaHoraInicio().atZone(zone);

		if (ahora.isAfter(inicio)) {
			throw new Exception("Ya no puede inscribirse a este evento");
		}

		// capacidad de participantes
		int actuales = inscripcionRepo.countParticipantesEfectivos(e.getId());
		int nuevos = 1 + (dto.getInvitados() != null ? dto.getInvitados().size() : 0);
		Integer limite = e.getCantidadMaximaParticipantes();
		if (limite != null && actuales + nuevos > limite) throw new Exception("No hay más lugar en el subespacio");

		// límite de invitados por inscripción
		if (e.getCantidadMaximaInvitados() != null && dto.getInvitados() != null &&
				dto.getInvitados().size() > e.getCantidadMaximaInvitados()) throw new Exception("Puede tener hasta " + e.getCantidadMaximaInvitados() + " invitados");

		boolean publico = e.getSubEspacio().getEspacio().getTipoEspacio().getNombre().equalsIgnoreCase("Público");

		if (!publico) {
			Usuario propietario = null;

			List<Usuario> props = e.getSubEspacio().getEspacio().getAdministradoresEspacio().stream().filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorEspacio().getNombre().equalsIgnoreCase("Propietario")).map(a -> a.getUsuario()).toList();

			if (!props.isEmpty()) {
				propietario = props.get(0);
			}

			if (propietario == null) {
				throw new Exception("No se encontró al propietario del espacio");
			}
		}
	}

	@Override 
	@Transactional
	public void inscribirse(DTOInscripcion dto) throws Exception {
		verificarDatosPrePagoBool(dto);

		Evento e = eventoRepo.findById(dto.getIdEvento())
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		String username = CurrentUser.getUsername()
			.orElseThrow(() -> new HttpErrorException(404, "Debe iniciar sesión antes de intentar inscribirse"));

		Usuario u = usuarioRepo.findByUsername(username)
			.orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

		//BigDecimal comision_inscripcion = parametroSistemaService.getDecimal("comision_inscripcion", new BigDecimal("0.1"));

		mercadoPagoSingleton.verifyPayments(dto.getDatosPago());
		for (DTOPago datosPago : dto.getDatosPago()) {
			registroSingleton.write("Pagos", "pago", "ejecucion", "Por inscripción a evento de ID " + e.getId() + " nombre '" + e.getNombre() + "'. ID de pago: " + datosPago.getPaymentId());
		}

		registroSingleton.write("Pagos", "cobro_comision", "ejecucion", "Por inscripción evento de ID " + e.getId() + " nombre '" + e.getNombre() + "'");

		Inscripcion ins = new Inscripcion();
		ins.setEvento(e);
		ins.setUsuario(u);

		ins.setFechaHoraAlta(LocalDateTime.now(ZONA_ARG));

		BigDecimal precioBase = e.getPrecioInscripcion().add(e.getAdicionalPorInscripcion())
				.multiply(BigDecimal.valueOf(1 + dto.getInvitados().size()));

		ins.setPrecioInscripcion(
				precioBase.multiply(getComisionInscripcion(precioBase).add(BigDecimal.valueOf(1)))
		);

		ins.setPermitirDevolucionCompleta(false);

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

		registroSingleton.write("Eventos", "inscripcion", "creacion", "A evento de ID " + e.getId() + " nombre '" + e.getNombre() + "'");
	}

	@Override
	@Transactional
	public void desinscribirse(long idEvento) throws Exception {
	    String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe autenticarse para poder cancelar una inscripción"));
	
	    Inscripcion ins = inscripcionRepo.findActivaByEventoIdAndUsuarioUsername(idEvento, username)
	    .orElseThrow(() -> new HttpErrorException(404, "No existe inscripción activa"));

		List<PorcentajeReintegroCancelacionInscripcion> rr = ins.getEvento().getPorcentajesReintegroCancelacion().stream().filter(p -> p.getFechaHoraBaja() == null).toList();
		Duration d = Duration.between(ins.getEvento().getFechaHoraInicio(), LocalDateTime.now());
		long minutosPendientes = Math.abs(d.toMinutes());
		int porcentajeDevolucion = rr.stream().filter(p -> p.getMinutosLimite() <= minutosPendientes).max(Comparator.comparing(PorcentajeReintegroCancelacionInscripcion::getMinutosLimite)).orElse(PorcentajeReintegroCancelacionInscripcion.builder().porcentaje(BigDecimal.ZERO).build()).getPorcentaje().intValue();

		if (ins.getPermitirDevolucionCompleta()) {
			porcentajeDevolucion = 100;
		}

		List<ComprobantePago> pagos = ins.getComprobantePagos();
		for (ComprobantePago c : pagos) {
			mercadoPagoSingleton.refundPayment(c, porcentajeDevolucion);
			registroSingleton.write("Pagos", "devolucion", "ejecucion", "Por cancelación de inscripción a evento de ID " + ins.getEvento().getId() + " nombre '" + ins.getEvento().getNombre() + "'");
		}
		registroSingleton.write("Pagos", "pago_comision", "ejecucion", "Por cancelación de inscripción a evento de ID " + ins.getEvento().getId() + " nombre '" + ins.getEvento().getNombre() + "'");
	
	    ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");
	    ins.setFechaHoraBaja(LocalDateTime.now(zone));
	    
	    inscripcionRepo.save(ins);

		registroSingleton.write("Eventos", "inscripcion", "eliminacion", "A evento de ID " + ins.getEvento().getId() + " nombre '" + ins.getEvento().getNombre() + "' por parte del mismo usuario");
	}

    @Override
    @Transactional
    public Number obtenerMontoDevolucionCancelacion(long idEvento, String username) throws Exception {
		Evento e = eventoRepo.findByIdForDetalle(idEvento)
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		Inscripcion inscripcion = inscripcionRepo.findActivaByEventoIdAndUsuarioUsername(idEvento, username).orElseThrow(() -> new Exception("No se encontró su inscripción"));

		BigDecimal totalPagado = inscripcion.getPrecioInscripcion();

		if (inscripcion.getPermitirDevolucionCompleta()) {
			return totalPagado;
		}

		if (totalPagado.signum() <= 0) return BigDecimal.ZERO;

		long minutosHastaInicio = Math.max(
			0L,
			java.time.Duration.between(LocalDateTime.now(), e.getFechaHoraInicio()).toMinutes()
		);

		//BigDecimal factor = BigDecimal.ZERO;

		/*if (e.getPorcentajesReintegroCancelacion() != null && !e.getPorcentajesReintegroCancelacion().isEmpty()) {
			Optional<PorcentajeReintegroCancelacionInscripcion> mejor = e.getPorcentajesReintegroCancelacion().stream()
				.filter(p -> p.getMinutosLimite() != null && minutosHastaInicio <= p.getMinutosLimite())
				.max(Comparator.comparing(PorcentajeReintegroCancelacionInscripcion::getMinutosLimite));

			if (mejor.isPresent() && mejor.get().getPorcentaje() != null) {
				factor = mejor.get().getPorcentaje().divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
			}
		}*/

		List<PorcentajeReintegroCancelacionInscripcion> rr = inscripcion.getEvento().getPorcentajesReintegroCancelacion().stream().filter(p -> p.getFechaHoraBaja() == null).toList();
		Duration d = Duration.between(inscripcion.getEvento().getFechaHoraInicio(), LocalDateTime.now());
		long minutosPendientes = Math.abs(d.toMinutes());
		BigDecimal factor = rr.stream().filter(p -> p.getMinutosLimite() <= minutosPendientes).max(Comparator.comparing(PorcentajeReintegroCancelacionInscripcion::getMinutosLimite)).orElse(PorcentajeReintegroCancelacionInscripcion.builder().porcentaje(BigDecimal.ZERO).build()).getPorcentaje();

		if (inscripcion.getPermitirDevolucionCompleta()) {
			factor = new BigDecimal(100);
		}
		factor = factor.divide(BigDecimal.valueOf(100));

		return totalPagado.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }


	@Override
	@Transactional
	public DTODatosModificarEvento obtenerDatosModificacionEvento(long idEvento) throws Exception {
		Evento e = eventoRepo.findByIdForDetalle(idEvento)
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		Usuario usuario = usuarioRepo.findByUsername(username).orElseThrow(() -> new Exception("Usuario no encontrado"));

		List<EventoEstado> ees = e.getEventosEstado().stream().filter(es -> es.getFechaHoraBaja() == null).toList();

		if (ees.size() != 1) {
			throw new Exception("El evento se encuentra en un estado no válido");
		}

		if (!ees.get(0).getEstadoEvento().getNombre().equalsIgnoreCase("Aceptado")) {
			throw new Exception("No se puede modificar el evento, pues el mismo no se encuentra en estado 'Aceptado'");
		}

		// Validar si el usuario actual es administrador del evento
		boolean esAdministrador = eventoRepo.existsByEventoIdAndAdministradorUsername(idEvento, username);

		// Validar si el usuario actual es organizador del evento
		boolean esOrganizador = e.getOrganizador() != null && e.getOrganizador().getUsername().equals(username);

		// Si fuera en un espacio público y el usuario es admin del sistema
		if (e.getSubEspacio().getEspacio().getTipoEspacio().getNombre().equalsIgnoreCase("público")) {
			if (usuario.getPermisos().contains("AdministracionEspaciosPublicos")) {
				esAdministrador = true;
			}
		} else { // Si no tuviera permiso para administrar un espacio privado
			if (!usuario.getPermisos().contains("AdministracionEspaciosPrivados")) {
				throw new Exception("No tiene permiso para modificar eventos");
			}
		}

		if (!esAdministrador && !esOrganizador) {
			throw new Exception("No tiene permiso para modificar este evento");
		}

		int participantes = inscripcionRepo.countParticipantesEfectivos(e.getId());
		int maxInvPorInscripcion = inscripcionRepo.maxInvitadosPorInscripcionVigente(e.getId());

		List<DTODatosModificarEvento.ItemIdNombre> disciplinas = new ArrayList<>();
		if (e.getDisciplinasEvento() != null) {
			for (DisciplinaEvento de : e.getDisciplinasEvento().stream().filter(de -> de.getFechaHoraBaja() == null).toList()) {
			disciplinas.add(new DTODatosModificarEvento.ItemIdNombre(
				de.getDisciplina().getId(), de.getDisciplina().getNombre()));
			}
		}

		List<DTODatosModificarEvento.RangoReintegro> rangos = new ArrayList<>();
		if (e.getPorcentajesReintegroCancelacion() != null) {
			e.getPorcentajesReintegroCancelacion().stream()
				.filter(p -> p.getFechaHoraBaja() == null)
				.sorted(Comparator.comparing(PorcentajeReintegroCancelacionInscripcion::getMinutosLimite).reversed())
				.forEach(p -> {
				int[] dhm = splitMinutes(p.getMinutosLimite());
				rangos.add(DTODatosModificarEvento.RangoReintegro.builder()
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

		return DTODatosModificarEvento.builder()
			.nombre(e.getNombre() != null ? e.getNombre() : "")
			.descripcion(e.getDescripcion() != null ? e.getDescripcion() : "")
			.nombreEspacio(e.getSubEspacio().getEspacio().getNombre())
			.nombreSubespacio(e.getSubEspacio().getNombre())
			.fechaHoraDesde(TimeUtil.toMillis(e.getFechaHoraInicio()))
			.fechaHoraHasta(TimeUtil.toMillis(e.getFechaHoraFin()))
			.adicionalPorInscripcion(e.getAdicionalPorInscripcion())
			.disciplinas(disciplinas)
			.precioInscripcion(e.getPrecioInscripcion() != null ? e.getPrecioInscripcion() : BigDecimal.ZERO)
			.comisionInscripcion(BigDecimal.valueOf(0.12))
			.cantidadMaximaParticipantes(e.getCantidadMaximaParticipantes() != null ? e.getCantidadMaximaParticipantes() : 0)
			.cantidadMaximaInvitados(e.getCantidadMaximaInvitados() != null ? e.getCantidadMaximaInvitados() : 0)
			.cantidadParticipantesActual(participantes)
			.cantidadMaximaInvitadosPorInvitacionEfectiva(maxInvPorInscripcion)
			.superevento(e.getSuperEvento() != null
				? new DTODatosModificarEvento.Superevento(
					e.getSuperEvento().getId(),
					e.getSuperEvento().getNombre() != null ? e.getSuperEvento().getNombre() : "",
					e.getSuperEvento().getDescripcion() != null ? e.getSuperEvento().getDescripcion() : "")
				: null)
			.rangosReintegro(rangos)
			.espacioPublico(null)
			.administradorEspacio(false)
			.administradorEvento(esAdministrador)
			.organizadorEvento(esOrganizador)
			.build();


	}

	@Override
	@Transactional
	public void modificarEvento(DTOModificarEvento dto) throws Exception {
		Evento e = eventoRepo.findById(dto.getId())
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		Usuario usuario = usuarioRepo.findByUsername(username).orElseThrow(() -> new Exception("Usuario no encontrado"));

		List<EventoEstado> ees = e.getEventosEstado().stream().filter(es -> es.getFechaHoraBaja() == null).toList();

		if (ees.size() != 1) {
			throw new Exception("El evento se encuentra en un estado no válido");
		}

		if (!ees.get(0).getEstadoEvento().getNombre().equalsIgnoreCase("Aceptado")) {
			throw new Exception("No se puede modificar el evento, pues el mismo no se encuentra en estado 'Aceptado'");
		}

		// Validar si el usuario actual es administrador del evento
		boolean esAdministrador = eventoRepo.existsByEventoIdAndAdministradorUsername(dto.getId(), username);

		// Validar si el usuario actual es organizador del evento
		boolean esOrganizador = e.getOrganizador() != null && e.getOrganizador().getUsername().equals(username);

		// Si fuera en un espacio público y el usuario es admin del sistema
		if (e.getSubEspacio().getEspacio().getTipoEspacio().getNombre().equalsIgnoreCase("público")) {
			if (usuario.getPermisos().contains("AdministracionEspaciosPublicos")) {
				esAdministrador = true;
			}
		} else { // Si no tuviera permiso para administrar un espacio privado
			if (!usuario.getPermisos().contains("AdministracionEspaciosPrivados")) {
				throw new Exception("No tiene permiso para modificar eventos");
			}
		}

		if (!esAdministrador && !esOrganizador) {
			throw new Exception("No tiene permiso para modificar este evento");
		}


		// Nombre y descripción
		if (dto.getNombre().length() > 50 || dto.getNombre().isEmpty()) {
			throw new Exception("El nombre debe tener entre 1 y 50 caracteres");
		}
		e.setNombre(dto.getNombre());

		if (dto.getDescripcion().length() > 500) {
			throw new Exception("La descripción no puede tener más de 500 caracteres");
		}
		e.setDescripcion(dto.getDescripcion());


		// Disciplinas
		if (dto.getDisciplinas() != null) {
			if (e.getDisciplinasEvento() == null) e.setDisciplinasEvento(new ArrayList<>());
			List<DisciplinaEvento> disciplinaEventos = e.getDisciplinasEvento();

			// Remover las eliminadas
			int i = 0;
			for (DisciplinaEvento de : disciplinaEventos) {
				if (de.getFechaHoraBaja() != null) continue;

				boolean encontrada = false;
				for (DTOModificarEvento.ItemIdNombre it : dto.getDisciplinas()) {
					if (Objects.equals(de.getDisciplina().getId(), it.getId())) {
						encontrada = true;
						break;
					}
				}
				if (!encontrada) {
					de.setFechaHoraBaja(LocalDateTime.now());
					disciplinaEventos.set(i, de);
				}
				i += 1;
			}

			// Agregar las nuevas
			for (DTOModificarEvento.ItemIdNombre it : dto.getDisciplinas()) {
				Disciplina d = disciplinaBaseRepo.findById(it.getId())
						.orElseThrow(() -> new HttpErrorException(400, "Disciplina no encontrada: " + it.getNombre()));

				boolean encontrada = false;
				for (DisciplinaEvento de : disciplinaEventos) {
					if (de.getFechaHoraBaja() != null) continue;
					if (Objects.equals(de.getDisciplina().getId(), it.getId())) {
						encontrada = true;
						break;
					}
				}

				if (!encontrada) {
					disciplinaEventos.add(DisciplinaEvento.builder()
							.evento(e).disciplina(d).fechaHoraAlta(LocalDateTime.now()).build());
				}
			}

			e.setDisciplinasEvento(disciplinaEventos);
		}


		// Precios
		if (dto.getPrecioInscripcion().doubleValue() < 0) {
			throw new Exception("No puede establecer un precio de inscripción negativo");
		}
		e.setPrecioInscripcion(dto.getPrecioInscripcion());


		// Participantes e invitados
		if (dto.getCantidadMaximaParticipantes() < 2) {
			throw new Exception("Debe haber al menos 2 participantes en el evento");
		}
		// Si hay más participantes de los que se está configurando ahora para que haya, cancelar las inscripciones más recientes
		int participantes = inscripcionRepo.countParticipantesEfectivos(e.getId());
		if (dto.getCantidadMaximaParticipantes() < e.getCantidadMaximaParticipantes() && dto.getCantidadMaximaParticipantes() < participantes) {
			List<Inscripcion> inscripciones = inscripcionRepo.findActivasByEventoId(e.getId()).stream().sorted(Comparator.comparing(Inscripcion::getFechaHoraAlta).reversed()).toList();
			int participantes_removidos = 0;
			for (Inscripcion ins : inscripciones) {
				List<ComprobantePago> pagos = ins.getComprobantePagos();
				for (ComprobantePago c : pagos) {
					mercadoPagoSingleton.refundPayment(c);
					registroSingleton.write("Pagos", "devolucion", "ejecucion", "Por cancelación de inscripción de usuario de username '" + username + "' a evento de ID " + ins.getEvento().getId() + " nombre '" + ins.getEvento().getNombre() + "' por reducción de cantidad máxima de participantes del evento");
				}
				registroSingleton.write("Pagos", "pago_comision", "ejecucion", "Por cancelación de inscripción de usuario de username '" + username + "' a evento de ID " + ins.getEvento().getId() + " nombre '" + ins.getEvento().getNombre() + "' por reducción de cantidad máxima de participantes del evento");

				ins.setFechaHoraBaja(LocalDateTime.now());
				inscripcionRepo.save(ins);

				mailService.enviar(ins.getUsuario().getMail(), "evtnet - Inscripción cancelada", "Su inscripción al evento '" + e.getNombre() + "' ha sido cancelada por un administrador. Se le devolvió su dinero.");
				registroSingleton.write("Eventos", "inscripcion", "eliminacion", "Cancelación de inscripción de usuario de username '" + username + "' a evento de ID " + ins.getEvento().getId() + " nombre '" + ins.getEvento().getNombre() + "' por reducción de cantidad máxima de participantes del evento");

				participantes_removidos += 1 + ins.getInvitados().size();
				if (participantes_removidos >= e.getCantidadMaximaParticipantes() - dto.getCantidadMaximaParticipantes()) {
					break;
				}
			}
		}

		e.setCantidadMaximaParticipantes(dto.getCantidadMaximaParticipantes());

		if (dto.getCantidadMaximaInvitados() < 0) {
			throw new Exception("No puede establecer una cantidad máxima de invitados negativa");
		}
		e.setCantidadMaximaInvitados(dto.getCantidadMaximaInvitados());


		// Superevento
		if (dto.getCrearSuperevento()) {
			// Crear superevento
			if (dto.getSuperevento().getNombre().length() > 50 || dto.getSuperevento().getNombre().isEmpty()) {
				throw new Exception("El nombre del superevento debe tener entre 1 y 50 caracteres");
			}
			if (dto.getSuperevento().getDescripcion().length() > 500) {
				throw new Exception("La descripción del superevento no puede tener más de 500 caracteres");
			}

			SuperEvento s = SuperEvento.builder()
				.nombre(dto.getSuperevento().getNombre())
				.descripcion(dto.getSuperevento().getDescripcion())
				.fechaHoraAlta(LocalDateTime.now())
				.build();

			TipoAdministradorSuperEvento t = tipoAdminSuperEventoRepo.findByNombreIgnoreCase("Organizador").orElseThrow(() -> new Exception("No se pudo generar el superevento"));

			s = superEventoRepo.save(s);

			AdministradorSuperEvento administradorSuperEvento = AdministradorSuperEvento.builder()
					.superEvento(s)
					.usuario(usuario)
					.tipoAdministradorSuperEvento(t)
				.build();
			administradorSuperEvento = administradorSuperEventoRepo.save(administradorSuperEvento);

			e.setSuperEvento(s);
		} else {
			if (dto.getSuperevento() == null || dto.getSuperevento().getId() == null) {
				// No es parte de superevento
				if (e.getSuperEvento() != null) {
					// Permitir que se desinscriba con devolución completa
					List<Inscripcion> inscripciones = inscripcionRepo.findActivasByEventoId(e.getId()).stream().sorted(Comparator.comparing(Inscripcion::getFechaHoraAlta).reversed()).toList();
					for (Inscripcion ins : inscripciones) {
						ins.setPermitirDevolucionCompleta(true);
						inscripcionRepo.save(ins);
					}
				}

				e.setSuperEvento(null);
			} else {
				// Es parte de un superevento
				SuperEvento s = superEventoRepo.findById(dto.getSuperevento().getId()).orElseThrow(() -> new Exception("No se encontró el superevento indicado"));

				e.setSuperEvento(s);
			}
		}


		// Rangos de reintegro
		if (esOrganizador) {
			List<PorcentajeReintegroCancelacionInscripcion> rr = new ArrayList<>(e.getPorcentajesReintegroCancelacion());

			for (DTOModificarEvento.RangoReintegro r2 : dto.getRangosReintegro()) {
				if (r2.getDias() < 0 || r2.getHoras() < 0 || r2.getHoras() > 23 || r2.getMinutos() < 0 || r2.getMinutos() > 59) {
					throw new Exception("Valores de tiempo inválidos");
				}
				if (r2.getPorcentaje() < 0 || r2.getPorcentaje() > 100) {
					throw new Exception("El porcentaje debe estar entre 0 y 100");
				}
			}

			// Quitar los que no estén en la nueva lista
			int i = 0;
			for (PorcentajeReintegroCancelacionInscripcion r : rr) {
				if (r.getFechaHoraBaja() != null) continue;
				boolean encontrado = false;
				for (DTOModificarEvento.RangoReintegro r2 : dto.getRangosReintegro()) {
					int minutos = r2.getMinutos() + r2.getHoras() * 60 + r2.getDias() * 24 * 60;
					if (r.getMinutosLimite() == minutos && r.getPorcentaje().intValue() == r2.getPorcentaje()) {
						encontrado = true;
						break;
					}
				}
				if (!encontrado) {
					r.setFechaHoraBaja(LocalDateTime.now());
					r = porcentajeReintegroCancelacionInscripcionRepo.save(r);
					rr.set(i, r);
				}
				i+=1;
			}

			// Agregar los nuevos
			for (DTOModificarEvento.RangoReintegro r2 : dto.getRangosReintegro()) {
				boolean encontrado = false;
				int minutos = r2.getMinutos() + r2.getHoras() * 60 + r2.getDias() * 24 * 60;
				for (PorcentajeReintegroCancelacionInscripcion r : rr) {
					if (r.getFechaHoraBaja() != null) continue;
					if (r.getMinutosLimite() == minutos && r.getPorcentaje().intValue() == r2.getPorcentaje()) {
						encontrado = true;
						break;
					}
				}
				if (!encontrado) {
					PorcentajeReintegroCancelacionInscripcion r = PorcentajeReintegroCancelacionInscripcion.builder()
							.evento(e)
							.fechaHoraAlta(LocalDateTime.now())
							.porcentaje(new BigDecimal(r2.getPorcentaje()))
							.minutosLimite(minutos)
							.build();
					r = porcentajeReintegroCancelacionInscripcionRepo.save(r);
					rr.add(r);
				}
			}

			e.setPorcentajesReintegroCancelacion(rr);

		}

		eventoRepo.save(e);

		registroSingleton.write("Eventos", "evento", "modificacion", "Evento de ID " + e.getId() + " nombre '" + e.getNombre() + "'");
		if (dto.getCrearSuperevento()) {
			registroSingleton.write("Eventos", "superevento", "creacion", "Superevento de ID " + e.getSuperEvento().getId() + " nombre '" + e.getSuperEvento().getNombre() + "' creado vinculado al evento de ID " + e.getId() + " nombre '" + e.getNombre() + "'");
		}
	}



    @Override
    @Transactional
    public DTOInscripcionesEvento obtenerInscripciones(long idEvento, String texto) throws Exception {
		Evento e = eventoRepo.findById(idEvento)
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		String currentUser = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe autenticarse primero"));

		boolean esAdministrador = eventoRepo.existsByEventoIdAndAdministradorUsername(idEvento, currentUser);
		boolean esOrganizador = e.getOrganizador() != null &&
			e.getOrganizador().getUsername().equals(currentUser);

		boolean esEncargado = false;
		Usuario ese = e.getSubEspacio().getEncargadoSubEspacio();
		if (ese != null) {
			esEncargado = ese.getUsername().equals(currentUser);
		}

		if (!esAdministrador && !esOrganizador && !esEncargado) {
			throw new Exception("No tiene permiso para acceder a esta información");
		}

		var inscripciones = inscripcionRepo.findByEventoIdAndFiltro(idEvento, texto).stream()
			.map(i -> DTOInscripcionesEvento.InscripcionDTO.builder()
				.id(i.getId())
				.usuario(DTOInscripcionesEvento.UsuarioDTO.builder()
					.username(i.getUsuario().getUsername())
					.nombre(i.getUsuario().getNombre())
					.apellido(i.getUsuario().getApellido())
					.dni(i.getUsuario().getDni())
					.build())
				.fechaInscripcion(i.getFechaHoraAlta())
				.fechaCancelacionInscripcion(i.getFechaHoraBaja())
				.transferencias(i.getComprobantePagos().stream()
					.map(c -> DTOInscripcionesEvento.TransferenciaDTO.builder()
						.numero(c.getNumero())
						.monto(c.getItems().stream()
							.filter(it -> it.getCobro().getUsername().equals(i.getUsuario().getUsername())
								|| it.getPago().getUsername().equals(i.getUsuario().getUsername()))
							.map(it ->
								it.getMontoUnitario()
									.multiply(BigDecimal.valueOf(it.getCantidad()))
									.multiply(BigDecimal.valueOf(
										it.getCobro().getUsername().equals(i.getUsuario().getUsername()) ? -1f : 1f
									))
							).reduce(BigDecimal.ZERO, BigDecimal::add)).build()
					).toList()
				)
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
			.esEncargado(esEncargado)
			.inscripciones(inscripciones)
			.build();
	}



	@Override
	@Transactional
	public void cancelarInscripcion(long idInscripcion) throws Exception {
		Inscripcion ins = inscripcionRepo.findById(idInscripcion)
			.orElseThrow(() -> new HttpErrorException(404, "Inscripción no encontrada"));

		Evento e = ins.getEvento();

		String currentUser = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe autenticarse primero"));

		boolean esAdministrador = eventoRepo.existsByEventoIdAndAdministradorUsername(e.getId(), currentUser);
		boolean esOrganizador = e.getOrganizador() != null &&
				e.getOrganizador().getUsername().equals(currentUser);

		if (!esAdministrador && !esOrganizador) {
			throw new Exception("Debe ser administrador del evento para poder cancelar inscripciones");
		}

		// Guardar hora de baja con zona Argentina
		ZoneId zone = ZoneId.of("America/Argentina/Buenos_Aires");
		ins.setFechaHoraBaja(LocalDateTime.now(zone));

		inscripcionRepo.save(ins);

		mailService.enviar(ins.getUsuario().getMail(), "evtnet - Inscripción cancelada", "Su inscripción al evento '" + e.getNombre() + "' ha sido cancelada. Se le devolvió su dinero.");
		registroSingleton.write("Eventos", "inscripcion", "eliminacion", "Cancelación de inscripción de usuario de username '" + ins.getUsuario().getUsername() + "' a evento de ID " + ins.getEvento().getId() + " nombre '" + ins.getEvento().getNombre() + "' por decisión de un administrador u organizador del evento");
	}



	//Eliminada
	@Override
	@Transactional
	public DTODatosParaInscripcion obtenerDatosParaInscripcion(long idEvento, String username) throws Exception {
		if (true) throw new Exception("Función eliminada");

		Evento e = eventoRepo.findById(idEvento)
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		boolean esAdministrador = eventoRepo.existsByEventoIdAndAdministradorUsername(idEvento, username);
		boolean esOrganizador = e.getOrganizador() != null && e.getOrganizador().getUsername().equals(username);

		int cuposDisponibles = e.getCantidadMaximaParticipantes() - e.getInscripciones().stream().filter(i -> i.getFechaHoraBaja() == null).mapToInt(i -> 1 + i.getInvitados().size()).sum();

		return DTODatosParaInscripcion.builder()
			.nombreEvento(e.getNombre())
			.cantidadMaximaInvitados(e.getCantidadMaximaInvitados())
			.limiteParticipantes(cuposDisponibles)
			.esAdministrador(esAdministrador)
			.esOrganizador(esOrganizador)
			.build();
	}

	// Eliminada
	@Override
	@Transactional
	public List<DTOBusquedaUsuario> buscarUsuariosNoInscriptos(Long idEvento, String texto) throws Exception {
		if (true) throw new Exception("Función eliminada");

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

	// Eliminada
	@Override
	@Transactional
	public void inscribirUsuario(DTOInscripcion dto) throws Exception {
		if (true) throw new Exception("Función eliminada");

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
	public void dejarDeAdministrar(Long eventoId) throws Exception {
		Evento evento = eventoRepo.findById(eventoId).orElseThrow(() -> new Exception("No se encontró el evento"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

		List<AdministradorEvento> admins = evento.getAdministradoresEvento().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(username)).toList();

		if (admins.isEmpty()) {
			throw new Exception("No es administrador del evento");
		}

		AdministradorEvento admin = admins.get(0);

		if (admin.getTipoAdministradorEvento().getNombre().equals("Organizador")) {
			throw new Exception("No puede dejar de administrar el evento porque es su organizador. Pase el rol de organizador a otro administrador primero.");
		}

		admin.setFechaHoraBaja(LocalDateTime.now());

		administradorEventoRepo.save(admin);

		registroSingleton.write("Eventos", "administrador_evento", "eliminacion", "Dejó de administrar por cuenta propia al evento de ID " + evento.getId() + " nombre '" + evento.getNombre() + "'");
	}

	@Override
	@Transactional
	public DTOAdministradores obtenerAdministradores(long idEvento) throws Exception {
		Evento e = eventoRepo.findById(idEvento)
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

		boolean esOrganizador = e.getOrganizador() != null &&
			e.getOrganizador().getUsername().equals(username);

		if (!esOrganizador) {
			return  DTOAdministradores.builder().esOrganizador(false).build();
		}

		List<DTOAdministradores.AdministradorDTO> admins = e.getAdministradoresEvento().stream()
			.collect(java.util.stream.Collectors.groupingBy(a -> a.getUsuario().getUsername()))
			.entrySet().stream()
			.map(entry -> {
				Usuario user = entry.getValue().get(0).getUsuario();
				boolean vigente = entry.getValue().stream().anyMatch(a -> a.getFechaHoraBaja() == null);

				List<DTOAdministradores.HistoricoDTO> historico = entry.getValue().stream()
						.map(a -> DTOAdministradores.HistoricoDTO.builder()
								.fechaDesde(a.getFechaHoraAlta())
								.fechaHasta(a.getFechaHoraBaja())
								.organizador(a.getTipoAdministradorEvento().getNombre().equals("Organizador"))
								.build())
						.toList();

				return DTOAdministradores.AdministradorDTO.builder()
						.nombre(user.getNombre())
						.apellido(user.getApellido())
						.username(user.getUsername())
						.vigente(vigente)
						.historico(historico)
						.build();
			}).toList();

		return DTOAdministradores.builder()
			.esOrganizador(esOrganizador)
			.nombreEvento(e.getNombre())
			.administradores(admins)
			.build();
	}

	@Override
	@Transactional
	public List<DTOBusquedaUsuario> buscarUsuariosNoAdministradores(Long idEvento, String texto) throws Exception {
		Evento evento = eventoRepo.findById(idEvento).orElseThrow(() -> new Exception("No se encontró el evento"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

		boolean esOrganizador = evento.getAdministradoresEvento().stream()
				.filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorEvento().getNombre().equals("Organizador") && a.getUsuario().getUsername().equals(username))
				.count() > 0;

		if (!esOrganizador) {
			throw new Exception("Debe ser el organizador del evento para gestionar sus administradores");
		}

		// Generación de keywords
		List<String> keywords = Arrays.asList(texto.split("\\s"))
				.stream().filter(k -> k.length() > 2).toList();

		String jpql = "SELECT DISTINCT u FROM Usuario u WHERE u.fechaHoraBaja IS NULL";

		for (int i = 0; i < keywords.size(); i++) {
			jpql += " AND (" +
					"LOWER(TRIM(u.nombre)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%')) OR " +
					"LOWER(TRIM(u.apellido)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%')) OR " +
					"LOWER(TRIM(u.username)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%'))" +
					")";
		}

		TypedQuery<Usuario> query = entityManager.createQuery(jpql, Usuario.class);

		for (int i = 0; i < keywords.size(); i++) {
			query.setParameter("kw" + i, keywords.get(i));
		}

		List<Usuario> usuarios = query.getResultList();

		// Filtrar usuarios que ya son administradores activos
		List<String> usernamesAdmins = evento.getAdministradoresEvento().stream()
				.filter(a -> a.getFechaHoraBaja() == null)
				.map(a -> a.getUsuario().getUsername())
				.toList();

		return usuarios.stream()
			.filter(u -> !usernamesAdmins.contains(u.getUsername()))
			.map((Usuario u) -> DTOBusquedaUsuario.builder()
				.username(u.getUsername())
				.nombre(u.getNombre())
				.apellido(u.getApellido())
				.build()
			)
			.toList();
	}


	@Override
	@Transactional
	public void agregarAdministrador(long idEvento, String username) throws Exception {
		Evento e = eventoRepo.findById(idEvento)
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));
		Usuario u = usuarioRepo.findByUsername(username)
			.orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

		String currentUser = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

		boolean esOrganizador = e.getAdministradoresEvento().stream()
				.filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorEvento().getNombre().equals("Organizador") && a.getUsuario().getUsername().equals(currentUser))
				.count() > 0;

		if (!esOrganizador) {
			throw new Exception("Debe ser el organizador del superevento para gestionar sus administradores");
		}

		TipoAdministradorEvento tipoAdmin = tipoAdminRepo.findByNombreIgnoreCase("Administrador")
				.orElseThrow(() -> new Exception("No se pudo asignar el tipo de administrador"));

		// Evitar duplicados
		boolean yaEsAdmin = e.getAdministradoresEvento().stream()
			.anyMatch(a -> a.getUsuario().equals(u) && a.getFechaHoraBaja() == null);
			if (yaEsAdmin) {
				throw new Exception("El usuario ya es administrador del evento");
			}

		AdministradorEvento ae = AdministradorEvento.builder()
			.evento(e)
			.usuario(u)
			.tipoAdministradorEvento(tipoAdmin)
			.fechaHoraAlta(LocalDateTime.now())
			.build();

		administradorEventoRepo.save(ae);

		registroSingleton.write("Eventos", "administrador_evento", "creacion", "Usuario '" + username + "' agregado como administrador del evento de ID " + e.getId() + " nombre '" + e.getNombre() + "'");
	}

	@Override
	@Transactional
	public void quitarAdministrador(long idEvento, String usernameAdminAQuitar) throws Exception {
		Evento evento = eventoRepo.findById(idEvento).orElseThrow(() -> new Exception("No se encontró el evento"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

		boolean esOrganizador = evento.getAdministradoresEvento().stream()
				.filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorEvento().getNombre().equals("Organizador") && a.getUsuario().getUsername().equals(username))
				.count() > 0;

		if (!esOrganizador) {
			throw new Exception("Debe ser el organizador del evento para gestionar sus administradores");
		}

		List<AdministradorEvento> admins = evento.getAdministradoresEvento().stream()
				.filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(usernameAdminAQuitar))
				.toList();

		if (admins.size() == 0) {
			throw new Exception("El usuario no es administrador del evento");
		}

		AdministradorEvento admin = admins.get(0);

		if (admin.getTipoAdministradorEvento().getNombre().equals("Organizador")) {
			throw new Exception("No puede quitar al organizador. Primero debe transferir el rol de organizador a otro administrador.");
		}

		admin.setFechaHoraBaja(LocalDateTime.now());

		administradorEventoRepo.save(admin);

		registroSingleton.write("Eventos", "administrador_evento", "eliminacion", "Usuario '" + usernameAdminAQuitar + "' removido como administrador del evento de ID " + evento.getId() + " nombre '" + evento.getNombre() + "'");
	}

	@Override
	@Transactional
	public void entregarOrganizador(long idEvento, String usernameNuevoOrganizador) throws Exception {
		Evento e = eventoRepo.findById(idEvento)
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		Usuario nuevo = usuarioRepo.findByUsername(usernameNuevoOrganizador)
			.orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

		List<AdministradorEvento> organizadores = e.getAdministradoresEvento().stream()
				.filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorEvento().getNombre().equals("Organizador") && a.getUsuario().getUsername().equals(username))
				.toList();

		if (organizadores.size() == 0) {
			throw new Exception("Usted no es el organizador de este evento");
		}

		AdministradorEvento organizadorActual = organizadores.get(0);

		List<AdministradorEvento> nuevosOrganizadores = e.getAdministradoresEvento().stream()
				.filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(usernameNuevoOrganizador))
				.toList();

		if (nuevosOrganizadores.isEmpty()) {
			throw new Exception("El usuario al que intenta entregar el rol de organizador no es administrador del evento");
		}

		AdministradorEvento adminActual = nuevosOrganizadores.get(0);

		if (adminActual.getTipoAdministradorEvento().getNombre().equals("Organizador")) {
			throw new Exception("El usuario ya es el organizador");
		}

		TipoAdministradorEvento tipoOrganizador = tipoAdminRepo.findByNombreIgnoreCase("Organizador")
				.orElseThrow(() -> new Exception("No se encontró el tipo Organizador"));
		TipoAdministradorEvento tipoAdministrador = tipoAdminRepo.findByNombreIgnoreCase("Administrador")
				.orElseThrow(() -> new Exception("No se encontró el tipo Administrador"));

		// Cambiar el organizador
		organizadorActual.setFechaHoraBaja(LocalDateTime.now());

		AdministradorEvento nuevoOrganizador = AdministradorEvento.builder()
				.usuario(adminActual.getUsuario())
				.evento(e)
				.tipoAdministradorEvento(tipoOrganizador)
				.fechaHoraAlta(LocalDateTime.now())
				.build();


		// Cambiar el nuevo administrador
		adminActual.setFechaHoraBaja(LocalDateTime.now());

		AdministradorEvento nuevoAdmin = AdministradorEvento.builder()
				.usuario(organizadorActual.getUsuario())
				.evento(e)
				.tipoAdministradorEvento(tipoAdministrador)
				.fechaHoraAlta(LocalDateTime.now())
				.build();

		administradorEventoRepo.save(organizadorActual);
		administradorEventoRepo.save(nuevoOrganizador);
		administradorEventoRepo.save(adminActual);
		administradorEventoRepo.save(nuevoAdmin);

		registroSingleton.write("Eventos", "administrador_evento", "modificacion", "Transferencia de rol de organizador del evento de ID " + e.getId() + " nombre '" + e.getNombre() + "' de @" + username + " a @" + usernameNuevoOrganizador);

	}

	@Override
	public void cancelarEvento(long idEvento, String motivo) throws Exception {
		if (motivo.length() > 500) {
			throw new Exception("El motivo de cancelación no puede superar los 500 caracteres de longitud");
		}

		Evento evento = eventoRepo.findById(idEvento).orElseThrow(() -> new Exception("No se encontró el evento"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

		boolean esOrganizador = evento.getOrganizador().getUsername().equals(username);

		Usuario usuario = usuarioRepo.findByUsername(username).orElseThrow(() -> new Exception("Sesión no iniciada"));

		boolean esAdminSistema = usuario.getPermisos().stream().filter(p -> p.equalsIgnoreCase("CancelacionEventosAdmin")).count() == 1;

		if (!esOrganizador && !esAdminSistema) {
			throw new Exception("No tiene permiso dar de baja el evento");
		}

		List<EventoEstado> ees = evento.getEventosEstado().stream().filter(e -> e.getFechaHoraBaja() == null).toList();

		if (ees.size() != 1) {
			throw new Exception("El evento se encuentra en un estado no válido");
		}

		EventoEstado eeAnterior = ees.get(0);
		if (!eeAnterior.getEstadoEvento().getNombre().equalsIgnoreCase("Aceptado") && !eeAnterior.getEstadoEvento().getNombre().equalsIgnoreCase("En Revisión")) {
			throw new Exception("No se puede cancelar el evento, pues el mismo no se encuentra en estado 'Aceptado' ni 'En Revisión'");
		}

		eeAnterior.setFechaHoraBaja(LocalDateTime.now());

		EstadoEvento cancelado = estadoEventoRepo.findByNombreIgnoreCase("Cancelado").orElseThrow(() -> new Exception("No se pudo asignar el estado 'Cancelado' al evento"));

		EventoEstado eeNuevo = EventoEstado.builder()
				.evento(evento)
				.estadoEvento(cancelado)
				.descripcion(motivo)
				.fechaHoraAlta(LocalDateTime.now())
				.build();

		List<Inscripcion> inscripciones = inscripcionRepo.findActivasByEventoId(evento.getId()).stream().sorted(Comparator.comparing(Inscripcion::getFechaHoraAlta).reversed()).toList();
		for (Inscripcion ins : inscripciones) {
			List<ComprobantePago> pagos = ins.getComprobantePagos();
			for (ComprobantePago c : pagos) {
				mercadoPagoSingleton.refundPayment(c);
				registroSingleton.write("Pagos", "devolucion", "ejecucion", "Por cancelación de evento de ID '" + evento.getId() + "', nombre '" + evento.getNombre() + "' al que estaba inscripto el usuario de username '" + username + "'");
			}
			registroSingleton.write("Pagos", "pago_comision", "ejecucion", "Por cancelación de evento de ID '" + evento.getId() + "', nombre '" + evento.getNombre() + "' al que estaba inscripto el usuario de username '" + username + "'");

			ins.setFechaHoraBaja(LocalDateTime.now());
			inscripcionRepo.save(ins);

			mailService.enviar(ins.getUsuario().getMail(), "evtnet - Inscripción cancelada", "El evento '" + evento.getNombre() + "' ha sido cancelado. Se le devolvió su dinero.");
			registroSingleton.write("Eventos", "inscripcion", "eliminacion", "Cancelación de inscripción de usuario de username '" + username + "' a evento de ID " + ins.getEvento().getId() + " nombre '" + ins.getEvento().getNombre() + "' por cancelación del evento");
		}

		eventoEstadoRepo.save(eeAnterior);
		eventoEstadoRepo.save(eeNuevo);
		eventoRepo.save(evento);

		registroSingleton.write("Eventos", "evento", "eliminacion", "Evento de ID " + evento.getId() + " nombre '" + evento.getNombre() + "' por motivo: '" + motivo + "'" + (esAdminSistema && !esOrganizador ? " (Dado de baja por un administrador del sistema)" : ""));

	}


	@Override
	@Transactional
	public void denunciarEvento(DTODenunciaEvento dto) throws Exception{
		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe iniciar sesión"));

		Usuario denunciante = usuarioRepo.findByUsername(username)
			.orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

		Evento evento = eventoRepo.findById(dto.getIdEvento())
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		boolean inscripto = inscripcionRepo.countByEventoIdAndUsuarioUsernameAndFechaHoraBajaIsNull(evento.getId(), username) > 0;

		if (!inscripto) {
			throw new Exception("No está inscripto a este evento; debe estarlo para poder denunciarlo");
		}

		boolean hayDenunciaPrevia = !denunciaEventoRepo.findAll().stream()
				.filter(d -> d.getEvento().getId().equals(evento.getId()) && d.getDenunciante().getUsername().equals(username))
				.toList().isEmpty();

		if (hayDenunciaPrevia) {
			throw new Exception("Ya ha denunciado a este evento");
		}

		if (dto.getTitulo().isEmpty() || dto.getTitulo().length() > 50) {
			throw new Exception("El título debe tener entre 1 y 50 caracteres");
		}

		if (dto.getDescripcion().length() > 1000) {
			throw new Exception("La descripción no puede superar los 1000 caracteres");
		}

		DenunciaEvento d = DenunciaEvento.builder()
			.titulo(dto.getTitulo())
			.descripcion(dto.getDescripcion())
			.denunciante(denunciante)
			.evento(evento)
			.fechaHoraAlta(LocalDateTime.now())
			.build();

		// Estado inicial
		EstadoDenunciaEvento estadoInicial = estadoDenunciaRepo.findAll().stream()
			.filter(e -> e.getNombre().equalsIgnoreCase("Ingresado"))
			.findFirst()
			.orElseThrow(() -> new HttpErrorException(400, "Estado inicial no configurado"));

		d = denunciaEventoRepo.save(d);

		DenunciaEventoEstado dee = DenunciaEventoEstado.builder()
			.denunciaEvento(d)
			.estadoDenunciaEvento(estadoInicial)
			.fechaHoraDesde(LocalDateTime.now())
			.responsable(denunciante)
			.build();

		denunciaEventoEstadoRepo.save(dee);

		registroSingleton.write("Eventos", "denuncia", "creacion", "Evento de ID " + evento.getId() + " nombre '" + evento.getNombre() + "' denunciado");
	}

	@Override
	@Transactional
	public List<DTOEstadoDenunciaEventoCheck> obtenerEstadosDenuncias() {
		return estadoDenunciaRepo.findAll().stream()
			.filter(e -> e.getFechaHoraBaja() == null)
			.map(e -> DTOEstadoDenunciaEventoCheck.builder()
					.id(e.getId())
					.nombre(e.getNombre())
					.checked(!e.getNombre().equalsIgnoreCase("Finalizado"))
					.build()
		).toList();
	}

	@Override
	@Transactional
	public Page<DTODenunciaEventoSimple> buscarDenuncias(DTOBusquedaDenunciasEventos filtro, int page) throws Exception {
		Integer longitudPagina = parametroSistemaService.getInt("longitudPagina", 20);

		Pageable pageable = PageRequest.of(page, longitudPagina);
		Page<DenunciaEvento> results = denunciaEventoRepo.buscarDenuncias(
				filtro.getTexto(),
				filtro.getEstados(),
				filtro.getFechaIngresoDesde(),
				filtro.getFechaIngresoHasta(),
				filtro.getFechaCambioEstadoDesde(),
				filtro.getFechaCambioEstadoHasta(),
				filtro.getOrden().name(),
				pageable
		);

		return results
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
							: d.getEstados().stream().filter(e -> e.getFechaHoraHasta() == null).toList().get(0).getFechaHoraDesde())
					.fechaHoraIngreso(d.getEstados().isEmpty()
							? null
							: d.getEstados().stream().filter(e -> e.getEstadoDenunciaEvento().getNombre().equalsIgnoreCase("Ingresado")).toList().get(0).getFechaHoraDesde())
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
				.descripcion(e.getDescripcion() != null ? e.getDescripcion() : "")
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

		EstadoDenunciaEvento e = d.getEstados().stream().filter(ed -> ed.getFechaHoraHasta() == null).toList().getFirst().getEstadoDenunciaEvento();

		var estados = estadoDenunciaRepo.obtenerPosiblesTransiciones(e.getId()).stream()
			.map(ee -> DTODatosParaCambioEstadoDenuncia.EstadoDTO.builder()
				.id(ee.getId())
				.nombre(ee.getNombre())
				.build())
			.toList();
	

		return DTODatosParaCambioEstadoDenuncia.builder()
			.titulo(d.getTitulo())
			.estados(estados)
			.build();
	}

	@Override
	@Transactional
	public void cambiarEstadoDenuncia(DTOCambioEstadoDenuncia dto) throws Exception {
		DenunciaEvento d = denunciaEventoRepo.findById(dto.getIdDenuncia())
			.orElseThrow(() -> new HttpErrorException(404, "Denuncia no encontrada"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe iniciar sesión"));

		Usuario responsable = usuarioRepo.findByUsername(username)
			.orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

		EstadoDenunciaEvento nuevoEstado = estadoDenunciaRepo.findById(dto.getEstado())
			.orElseThrow(() -> new HttpErrorException(404, "Estado no encontrado"));

		DenunciaEventoEstado estadoActualVinculo = d.getEstados().stream().filter(ed -> ed.getFechaHoraHasta() == null).toList().getFirst();

		EstadoDenunciaEvento estadoActual = estadoActualVinculo.getEstadoDenunciaEvento();

		boolean esPosible = estadoDenunciaRepo.obtenerPosiblesTransiciones(estadoActual.getId()).stream().map(e -> e.getId()).toList().contains(nuevoEstado.getId());

		if (!esPosible) {
			throw new Exception("No es posible realizar esta transición");
		}

		if (dto.getDescripcion().isEmpty() || dto.getDescripcion().length() > 1000) {
			throw new Exception("La descripción debe tener entre 1 y 1000 caracteres");
		}

		// cerrar último estado
		estadoActualVinculo.setFechaHoraHasta(LocalDateTime.now());
		denunciaEventoEstadoRepo.save(estadoActualVinculo);

		// agregar nuevo
		DenunciaEventoEstado dee = DenunciaEventoEstado.builder()
			.denunciaEvento(d)
			.estadoDenunciaEvento(nuevoEstado)
			.descripcion(dto.getDescripcion())
			.responsable(responsable)
			.fechaHoraDesde(LocalDateTime.now())
			.build();

		denunciaEventoEstadoRepo.save(dee);

		registroSingleton.write("Eventos", "denuncia", "modificacion", "Cambio de estado a denuncia a evento de ID " + d.getEvento().getId() + " nombre '" + d.getEvento().getNombre() + "' de denunciante @" + d.getDenunciante().getUsername() + " al estado '" + nuevoEstado.getNombre() + "'");
	}

	@Override
	@Transactional
	public DTODatosParaDenunciarEvento obtenerDatosParaDenunciar(long idEvento) throws Exception {
		Evento e = eventoRepo.findById(idEvento)
			.orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

		String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe iniciar sesión"));

		boolean inscripto = inscripcionRepo.countByEventoIdAndUsuarioUsernameAndFechaHoraBajaIsNull(idEvento, username) > 0;
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

	@Override
	public void aprobarRechazarEvento(Long idEvento, String estado){
		Evento evento=this.eventoRepo.findById(idEvento).orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));
		EventoEstado eventoEstado=this.eventoEstadoRepo.findUltimoByEvento(idEvento);
		eventoEstado.setFechaHoraBaja(LocalDateTime.now());
		this.eventoEstadoRepo.save(eventoEstado);
		EstadoEvento estadoEvento=this.estadoEventoRepo.findByNombreIgnoreCase(estado).orElseThrow(() -> new HttpErrorException(404, "Estado de evento no encontrado"));
		EventoEstado eventoEstadoNuevo=EventoEstado.builder()
				.fechaHoraAlta(LocalDateTime.now())
				.estadoEvento(estadoEvento)
				.evento(evento)
				.build();
		this.eventoEstadoRepo.save(eventoEstadoNuevo);
	}

	@Override
	public void cancelarEventoEspacio(Long idEvento){
		Evento evento=this.eventoRepo.findById(idEvento).orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));
		EventoEstado eventoEstado=this.eventoEstadoRepo.findUltimoByEvento(idEvento);
		eventoEstado.setFechaHoraBaja(LocalDateTime.now());
		this.eventoEstadoRepo.save(eventoEstado);
		EstadoEvento estadoEvento=this.estadoEventoRepo.findByNombreIgnoreCase("Cancelado").orElseThrow(() -> new HttpErrorException(404, "Estado de evento no encontrado"));
		EventoEstado eventoEstadoNuevo=EventoEstado.builder()
				.fechaHoraAlta(LocalDateTime.now())
				.estadoEvento(estadoEvento)
				.evento(evento)
				.build();
		this.eventoEstadoRepo.save(eventoEstadoNuevo);
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

	private BigDecimal getComisionInscripcion(BigDecimal valor)throws Exception{
		ComisionPorInscripcion comisionInscripcion = this.comisionPorInscripcionRepo.findComisionByValor(valor.doubleValue());
		BigDecimal porcentaje = BigDecimal.ZERO;
		if (comisionInscripcion != null) porcentaje = comisionInscripcion.getPorcentaje();
		return porcentaje.divide(BigDecimal.valueOf(100f));
	}
	private BigDecimal getComisionOrganizacion(BigDecimal valor)throws Exception{
		ComisionPorOrganizacion comisionOrganizacion = this.comisionPorOrganizacionRepo.findComisionByValor(valor.doubleValue());
		BigDecimal porcentaje = BigDecimal.ZERO;
		if (comisionOrganizacion != null) porcentaje = comisionOrganizacion.getPorcentaje();
		return porcentaje.divide(BigDecimal.valueOf(100f));
	}
}
