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
			TipoAdministradorEventoRepository tipoAdminEventoRepo
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
    Evento e = eventoRepo.findByIdForDetalle(idEvento)
            .orElseThrow(() -> new HttpErrorException(404, "Evento no encontrado"));

    // ✅ cargar inscripciones activas
    List<Inscripcion> inscripcionesActivas = inscripcionRepo.findActivasByEventoId(e.getId());
    e.setInscripciones(inscripcionesActivas);

    // Inicializar colecciones LAZY
    if (e.getDisciplinasEvento() != null) {
        e.getDisciplinasEvento().forEach(de -> {
            if (de.getDisciplina() != null) de.getDisciplina().getNombre();
        });
    }

    String username = null;
    try {
        username = SecurityContextHolder.getContext().getAuthentication().getName();
    } catch (Exception ignored) {}

    // ✅ inscripto = solo si existe inscripción activa (sin fechaHoraBaja)
    boolean inscripto = (username != null) &&
            inscripcionRepo.countActivasByEventoIdAndUsuarioUsername(e.getId(), username) > 0;

    boolean administrador = false;
    if (username != null) {
        try {
            administrador = eventoRepo.existsByEventoIdAndAdministradorUsername(e.getId(), username);
        } catch (Exception ignored) {
            administrador = false;
        }
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

        double comision = 0.12;
        int diasHaciaAdelante = 30;

        return new DTODatosCreacionEvento(nombreEspacio, comision, null, null, diasHaciaAdelante);
    }

    @Override
    @Transactional
    public long crearEvento(DTOEventoCreate r) throws Exception {
		if (r.getFechaHoraInicio() == null || r.getFechaHoraFin() == null) {
			throw new HttpErrorException(400, "Fecha/hora de inicio y fin son requeridas");
		}

		Evento e = new Evento();
		e.setNombre(r.getNombre());
		e.setDescripcion(r.getDescripcion());
		e.setFechaHoraInicio(r.getFechaHoraInicio());
		e.setFechaHoraFin(r.getFechaHoraFin());
		e.setPrecioInscripcion(r.getPrecioInscripcion());
		e.setCantidadMaximaInvitados(r.getCantidadMaximaInvitados());
		e.setCantidadMaximaParticipantes(r.getCantidadMaximaParticipantes());
		e.setPrecioOrganizacion(r.getPrecioOrganizacion());

		// 🔹 Asignar organizador (usuario autenticado)
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Usuario organizador = usuarioRepo.findByUsername(username)
			.orElseThrow(() -> new HttpErrorException(404, "Usuario no encontrado"));

		TipoAdministradorEvento tipoAdmin = tipoAdminEventoRepo.findByNombreIgnoreCase("Organizador")
			.orElseThrow(() -> new Exception("No se pudo vincular al usuario con el evento"));

		// 🔹 Agregar automáticamente al organizador como administrador del evento
		AdministradorEvento admin = AdministradorEvento.builder()
			.evento(e)
			.usuario(organizador)
			.tipoAdministradorEvento(tipoAdmin)
			.fechaHoraAlta(LocalDateTime.now())
			.build();

		if (e.getAdministradoresEvento() == null) {
			e.setAdministradoresEvento(new ArrayList<>());
		}
		e.getAdministradoresEvento().add(admin);

		if (r.getEspacioId() != null) {
			Espacio esp = espacioRepo.findById(r.getEspacioId())
					.orElseThrow(() -> new HttpErrorException(404, "Espacio no encontrado"));
			//e.setEspacio(esp);
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
