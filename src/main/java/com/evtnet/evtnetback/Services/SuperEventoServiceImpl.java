package com.evtnet.evtnetback.Services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.evtnet.evtnetback.Entities.Evento;
import com.evtnet.evtnetback.Entities.EventoEstado;
import com.evtnet.evtnetback.Entities.SuperEvento;
import com.evtnet.evtnetback.Entities.TipoAdministradorSuperEvento;
import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Entities.AdministradorSuperEvento;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.Repositories.SuperEventoRepository;
import com.evtnet.evtnetback.Repositories.UsuarioRepository;
import com.evtnet.evtnetback.Repositories.AdministradorSuperEventoRepository;
import com.evtnet.evtnetback.Repositories.TipoAdministradorSuperEventoRepository;
import com.evtnet.evtnetback.Repositories.EventoRepository;
import com.evtnet.evtnetback.dto.supereventos.DTOAdministradoresSuperevento;
import com.evtnet.evtnetback.dto.supereventos.DTOBusquedaAdministrados;
import com.evtnet.evtnetback.dto.supereventos.DTOBusquedaEvento;
import com.evtnet.evtnetback.dto.supereventos.DTOBusquedaMisSuperEventos;
import com.evtnet.evtnetback.dto.supereventos.DTOCrearSuperEvento;
import com.evtnet.evtnetback.dto.supereventos.DTOResultadoBusquedaMisSuperEventos;
import com.evtnet.evtnetback.dto.supereventos.DTOSuperEvento;
import com.evtnet.evtnetback.dto.supereventos.DTOSuperEventoEditar;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;
import com.evtnet.evtnetback.util.CurrentUser;
import com.evtnet.evtnetback.utils.TimeUtil;
import com.evtnet.evtnetback.util.RegistroSingleton;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Service
public class SuperEventoServiceImpl extends BaseServiceImpl <SuperEvento,Long> implements SuperEventoService  {

    private final SuperEventoRepository repo;
    private final RegistroSingleton registroSingleton;
    private final UsuarioRepository usuarioRepo;
    private final AdministradorSuperEventoRepository adminRepo;
    private final TipoAdministradorSuperEventoRepository tipoAdminRepo;
    private final EventoRepository eventoRepo;

    @PersistenceContext
    private EntityManager entityManager;

    public SuperEventoServiceImpl(
        BaseRepository<SuperEvento, Long> baseRepository, 
        SuperEventoRepository repo, 
        RegistroSingleton registroSingleton, 
        UsuarioRepository usuarioRepo, 
        AdministradorSuperEventoRepository adminRepo,
        TipoAdministradorSuperEventoRepository tipoAdminRepo,
        EventoRepository eventoRepo
    ) {
        super(baseRepository);
        this.repo = repo;
        this.registroSingleton = registroSingleton;
        this.usuarioRepo = usuarioRepo;
        this.adminRepo = adminRepo;
        this.tipoAdminRepo = tipoAdminRepo;
        this.eventoRepo = eventoRepo;
    }

    @Override
    public List<DTOBusquedaAdministrados> buscarAdministrados(String text) throws Exception {
        
        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("No se encontró al usuario"));
        
        List<SuperEvento> supereventos = repo.searchByUsuario_Username(username, text);

        ArrayList<DTOBusquedaAdministrados> ret = new ArrayList<>();

        supereventos.forEach(s -> {
            ret.add(DTOBusquedaAdministrados.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .build());
        });

        return ret;
    }

    @Override
    public List<DTOResultadoBusquedaMisSuperEventos> buscarMisSuperEventos(DTOBusquedaMisSuperEventos data) throws Exception {
        // Generación de keywords para buscar
        List<String> keywords = Arrays.asList(data.getTexto().split("\s"))
            .stream().filter(k -> k.length() > 2).toList();

        // Generación de la query

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        String jpql = "SELECT DISTINCT s FROM SuperEvento s JOIN s.administradorSuperEventos a WHERE s.fechaHoraBaja is null";

        for (int i = 0; i < keywords.size(); i++) {
            jpql += " AND (" + 
            "LOWER (TRIM(s.nombre)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%')) OR " + 
            "LOWER (TRIM(s.descripcion)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%'))" + 
            ")";
        }

        if (data.getFechaDesde() != null) {
            jpql += " AND s.fechaHoraAlta >= :fechaDesde";
        }

        if (data.getFechaHasta() != null) {
            jpql += " AND s.fechaHoraBaja <= :fechaHasta";
        }

        jpql += " AND a.usuario.username = :username";

        TypedQuery<SuperEvento> query = entityManager.createQuery(jpql, SuperEvento.class);

        for (int i = 0; i < keywords.size(); i++) {
            query.setParameter("kw" + i, keywords.get(i));
        }

        if (data.getFechaDesde() != null) {
            query.setParameter("fechaDesde", data.getFechaDesde());
        }

        if (data.getFechaHasta() != null) {
            query.setParameter("fechaHasta", data.getFechaHasta());
        }

        query.setParameter("username", username);

        List<SuperEvento> supereventos = query.getResultList();

        ArrayList<DTOResultadoBusquedaMisSuperEventos> ret = new ArrayList<>();

        for (SuperEvento s : supereventos) {

            List<Evento> eventos = s.getEventos().stream().filter(e -> {
                List<EventoEstado> historico = e.getEventosEstado().stream().filter(h -> h.getFechaHoraBaja() == null).toList();

                if (historico.size() > 0) {
                    return false;
                }

                if (!historico.get(0).getEstadoEvento().getNombre().equals("Aceptado")) {
                    return false;
                }
                return true;
            }).toList();

            int eventosTotales = eventos.size();
            int eventosFuturos = eventos.stream().filter(e -> e.getFechaHoraInicio().isAfter(LocalDateTime.now())).toList().size();

            LocalDateTime desde = eventos.stream()
                .map(Evento::getFechaHoraInicio)
                .min(LocalDateTime::compareTo)
                .orElse(null);

            LocalDateTime hasta = eventos.stream()
                .map(Evento::getFechaHoraFin)
                .max(LocalDateTime::compareTo)
                .orElse(null);

            ret.add(DTOResultadoBusquedaMisSuperEventos.builder()
                .id(s.getId())
                .nombre(s.getNombre())
                .fechaDesde(TimeUtil.toMillis(desde))
                .fechaHasta(TimeUtil.toMillis(hasta))
                .eventosFuturos(eventosFuturos)
                .eventosTotales(eventosTotales)
                .build());
        }

        return ret;
    }

    @Override
    public DTOSuperEvento obtenerSuperEvento(Long id) throws Exception {
        SuperEvento superEvento = repo.findById(id).orElseThrow(() -> new Exception("No se encontró el superevento"));

        if (superEvento.getFechaHoraBaja() != null) {
            throw new Exception("No se encontró el superevento");
        }

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        return DTOSuperEvento.builder()
            .nombre(superEvento.getNombre())
            .descripcion(superEvento.getDescripcion())
            .esAdministrador(superEvento.getAdministradorSuperEventos().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(username)).count() > 0)
            .eventos(superEvento.getEventos().stream().map(e -> {
                List<EventoEstado> historico = e.getEventosEstado().stream().filter(h -> h.getFechaHoraBaja() == null).toList();
                boolean cancelado = false;
                if (!historico.isEmpty() && historico.get(0).getEstadoEvento().getNombre().equals("Cancelado")) {
                    cancelado = true;
                }

                return DTOSuperEvento.DTOEvento.builder()
                    .id(e.getId())
                    .nombre(e.getNombre())
                    .fechaDesde(TimeUtil.toMillis(e.getFechaHoraInicio()))
                    .fechaHasta(TimeUtil.toMillis(e.getFechaHoraFin()))
                    .nombreEspacio(e.getSubEspacio().getEspacio().getNombre())
                    .esAdministrador(e.getAdministradoresEvento().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(username)).count() > 0)
                    .cancelado(cancelado)
                    .build();
                }).toList())
            .build();
    }

    @Override
    public long crearSuperEvento(DTOCrearSuperEvento data) throws Exception {

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        Usuario usuario = usuarioRepo.findByUsername(username).orElseThrow(() -> new Exception("Sesión no válida"));

        TipoAdministradorSuperEvento tipoAdministradorSuperEvento = tipoAdminRepo.findByNombreIgnoreCase("Organizador").orElseThrow(() -> new Exception("No se pudo vincular al organizador al evento"));

        if (data.getNombre().length() == 0 || data.getNombre().length() > 50) {
            throw new Exception("El nombre debe tener entre 1 y 50 caracteres");
        }

        if (data.getDescripcion().length() > 500) {
            throw new Exception("La descripción no puede superar los 500 caracteres");
        }

        SuperEvento superEvento = SuperEvento.builder()
            .nombre(data.getNombre())
            .descripcion(data.getDescripcion())
            .fechaHoraAlta(LocalDateTime.now())
            .build();

        superEvento = repo.save(superEvento);

        AdministradorSuperEvento admin = AdministradorSuperEvento.builder()
            .usuario(usuario)
            .superEvento(superEvento)
            .tipoAdministradorSuperEvento(tipoAdministradorSuperEvento)
            .fechaHoraAlta(LocalDateTime.now())
            .build();

        adminRepo.save(admin);

        registroSingleton.write("Eventos", "superevento", "creacion", "Superevento de ID " + superEvento.getId() + " nombre '" + superEvento.getNombre() + "'");

        return superEvento.getId();
    }

    @Override
    public DTOSuperEventoEditar obtenerSuperEventoEditar(Long id) throws Exception {
        SuperEvento superEvento = repo.findById(id).orElseThrow(() -> new Exception("No se encontró el superevento"));

        if (superEvento.getFechaHoraBaja() != null) {
            throw new Exception("No se encontró el superevento");
        }

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        boolean esOrganizador = superEvento.getAdministradorSuperEventos().stream().filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorSuperEvento().getNombre().equals("Organizador") && a.getUsuario().getUsername().equals(username)).count() > 0;

        return DTOSuperEventoEditar.builder()
            .id(superEvento.getId())
            .nombre(superEvento.getNombre())
            .descripcion(superEvento.getDescripcion())
            .esOrganizador(esOrganizador)
            .eventos(superEvento.getEventos().stream().map(e -> {
                List<EventoEstado> historico = e.getEventosEstado().stream().filter(h -> h.getFechaHoraBaja() == null).toList();
                boolean cancelado = false;
                if (!historico.isEmpty() && historico.get(0).getEstadoEvento().getNombre().equals("Cancelado")) {
                    cancelado = true;
                }

                if (!cancelado) {
                    String nombreEstado = historico.get(0).getEstadoEvento().getNombre();
                    if (nombreEstado.equals("Cancelado") || nombreEstado.equals("Rechazado")) {
                        cancelado = true;
                    }
                } 

                return DTOSuperEventoEditar.DTOEvento.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .fechaDesde(TimeUtil.toMillis(e.getFechaHoraInicio()))
                .fechaHasta(TimeUtil.toMillis(e.getFechaHoraFin()))
                .nombreEspacio(e.getSubEspacio().getEspacio().getNombre())
                .crear(false)
                .eliminar(false)
                .cancelado(cancelado)
                .build();
            }).toList())
            .build();
    }

    @Override
    public void editarSuperEvento(DTOSuperEventoEditar data) throws Exception {
        SuperEvento superEvento = repo.findById(data.getId()).orElseThrow(() -> new Exception("No se encontró el superevento"));

        if (superEvento.getFechaHoraBaja() != null) {
            throw new Exception("No se encontró el superevento");
        }

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        boolean esAdministrador = superEvento.getAdministradorSuperEventos().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(username)).count() > 0;

        if (!esAdministrador) {
            throw new Exception("No tiene permisos de administrador para este superevento");
        }
        
        if (data.getNombre().length() == 0 || data.getNombre().length() > 50) {
            throw new Exception("El nombre debe tener entre 1 y 50 caracteres");
        }

        if (data.getDescripcion().length() > 500) {
            throw new Exception("La descripción no puede superar los 500 caracteres");
        }

        superEvento.setNombre(data.getNombre());
        superEvento.setDescripcion(data.getDescripcion());

        superEvento = repo.save(superEvento);

        registroSingleton.write("Eventos", "superevento", "modificacion", "Superevento de ID " + superEvento.getId() + " nombre '" + superEvento.getNombre() + "'");

        String errorVinculacion = "";

        // TODO: testear vinculación y desvinculación de eventos

        for (DTOSuperEventoEditar.DTOEvento e : data.getEventos()) {
            if (e.getCrear()) {
                Optional<Evento> optEvento = eventoRepo.findById(e.getId());

                if (optEvento.isEmpty()) {
                    errorVinculacion += "No se encontró al evento a vincular '" + e.getNombre() + "'\n";
                    continue;
                }

                Evento evento = optEvento.get();

                if (evento.getSuperEvento() != null) {
                    errorVinculacion += "No se pudo vincular al evento '" + evento.getNombre() + "'' debido a que el mismo ya se encuentra vinculado a un superevento\n";
                    continue;
                }

                evento.setSuperEvento(superEvento);

                eventoRepo.save(evento);
                registroSingleton.write("Eventos", "evento", "modificacion", "Evento de ID " + evento.getId() + " nombre '" + evento.getNombre() + "' vinculado a superevento de ID " + superEvento.getId() + " nombre '" + superEvento.getNombre() + "'");
            } 
            
            if (e.getEliminar()) {
                Optional<Evento> optEvento = eventoRepo.findById(e.getId());

                if (!optEvento.isPresent()) {
                    errorVinculacion += "No se encontró al evento a desvincular '" + e.getNombre() + "'\n";
                    continue;
                }

                Evento evento = optEvento.get();

                if (evento.getSuperEvento() == null || evento.getSuperEvento().getId() != superEvento.getId()) {
                    errorVinculacion += "No se pudo desvincular al evento '" + evento.getNombre() + "'' debido a que el mismo no se encuentra vinculado al superevento\n";
                    continue;
                }

                evento.setSuperEvento(null);
                eventoRepo.save(evento);

                registroSingleton.write("Eventos", "evento", "modificacion", "Evento de ID " + evento.getId() + " nombre '" + evento.getNombre() + "' desvinculado de superevento de ID " + superEvento.getId() + " nombre '" + superEvento.getNombre() + "'");
            }
        }

        if (!errorVinculacion.equals("")) {
            throw new Exception("SuperEvento modificado. No se pudo completar todas las vinculaciones y/o desvinculaciones de eventos:\n" + errorVinculacion);
        }
    }

    @Override
    public void dejarDeAdministrar(Long supereventoId) throws Exception {
        SuperEvento superEvento = repo.findById(supereventoId).orElseThrow(() -> new Exception("No se encontró el superevento"));

        if (superEvento.getFechaHoraBaja() != null) {
            throw new Exception("No se encontró el superevento");
        }

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        List<AdministradorSuperEvento> admins = superEvento.getAdministradorSuperEventos().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(username)).toList();

        if (admins.size() == 0) {
            throw new Exception("No es administrador del superevento");
        }

        AdministradorSuperEvento admin = admins.get(0);

        if (admin.getTipoAdministradorSuperEvento().getNombre().equals("Organizador")) {
            throw new Exception("No puede dejar de administrar el superevento porque es su organizador. Pase el rol de organizador a otro administrador primero.");
        }

        admin.setFechaHoraBaja(LocalDateTime.now());

        adminRepo.save(admin);

        registroSingleton.write("Eventos", "administrador_superevento", "eliminacion", "Dejó de administrar por cuenta propia al superevento de ID " + superEvento.getId() + " nombre '" + superEvento.getNombre() + "'");
    }

    @Override
    public void baja(Long supereventoId) throws Exception {
        SuperEvento superEvento = repo.findById(supereventoId).orElseThrow(() -> new Exception("No se encontró el superevento"));

        if (superEvento.getFechaHoraBaja() != null) {
            throw new Exception("No se encontró el superevento");
        }

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        boolean esOrganizador = superEvento.getAdministradorSuperEventos().stream().filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorSuperEvento().getNombre().equals("Organizador") && a.getUsuario().getUsername().equals(username)).count() > 0;

        if (!esOrganizador) {
            throw new Exception("No puede dar de baja el superevento porque no es su organizador");
        }

        if (superEvento.getEventos().size() > 0) {
            throw new Exception("No se puede dar de baja el superevento porque el mismo tiene eventos vinculados. Primero debe desvincular todos los eventos");
        }

        superEvento.setFechaHoraBaja(LocalDateTime.now());

        repo.save(superEvento);

        registroSingleton.write("Eventos", "superevento", "eliminacion", "Superevento de ID " + superEvento.getId() + " nombre '" + superEvento.getNombre() + "'");

    }

    @Override
    public List<DTOBusquedaEvento> buscarEventosNoVinculados(Long idSuperevento, String texto) throws Exception {
        SuperEvento superEvento = repo.findById(idSuperevento).orElseThrow(() -> new Exception("No se encontró el superevento"));

        if (superEvento.getFechaHoraBaja() != null) {
            throw new Exception("No se encontró el superevento");
        }

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        boolean esAdministrador = superEvento.getAdministradorSuperEventos().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(username)).count() > 0;

        if (!esAdministrador) {
            throw new Exception("No tiene permisos de administrador para este superevento");
        }
        
        // Generación de keywords para buscar
        List<String> keywords = Arrays.asList(texto.split("\s"))
            .stream().filter(k -> k.length() > 2).toList();

        // Generación de la query

        String jpql = "SELECT DISTINCT e FROM Evento e JOIN e.administradoresEvento a WHERE 1=1";

        for (int i = 0; i < keywords.size(); i++) {
            jpql += " AND (" + 
            "LOWER (TRIM(e.nombre)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%')) OR " + 
            "LOWER (TRIM(e.descripcion)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%'))" + 
            ")";
        }

        jpql += " AND a.usuario.username = :username AND a.fechaHoraBaja IS NULL";
        jpql += " AND (e.superEvento IS NULL OR e.superEvento <> :superevento)";

        TypedQuery<Evento> query = entityManager.createQuery(jpql, Evento.class);

        for (int i = 0; i < keywords.size(); i++) {
            query.setParameter("kw" + i, keywords.get(i));
        }

        query.setParameter("username", username);
        query.setParameter("superevento", superEvento);

        List<Evento> eventos = query.getResultList();

        return eventos.stream().map(e -> DTOBusquedaEvento.builder()
            .id(e.getId())
            .nombre(e.getNombre())
            .fechaDesde(TimeUtil.toMillis(e.getFechaHoraInicio()))
            .fechaHasta(TimeUtil.toMillis(e.getFechaHoraFin()))
            .nombreEspacio(e.getSubEspacio().getEspacio().getNombre())
            .disciplinas(e.getDisciplinasEvento().stream().map(d -> 
                DTOBusquedaEvento.DTODisciplinas.builder()
                .id(d.getDisciplina().getId())
                .nombre(d.getDisciplina().getNombre())
                .build()).toList())
            .build()).toList();
    }

    @Override
    public DTOAdministradoresSuperevento obtenerAdministradores(Long idSuperevento) throws Exception {
        SuperEvento superEvento = repo.findById(idSuperevento).orElseThrow(() -> new Exception("No se encontró el superevento"));

        if (superEvento.getFechaHoraBaja() != null) {
            throw new Exception("No se encontró el superevento");
        }

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        boolean esOrganizador = superEvento.getAdministradorSuperEventos().stream()
            .filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorSuperEvento().getNombre().equals("Organizador") && a.getUsuario().getUsername().equals(username))
            .count() > 0;

        if (!esOrganizador) {
            throw new Exception("Debe ser el organizador del superevento para gestionar sus administradores");
        }

        List<DTOAdministradoresSuperevento.DTOAdministradores> administradores = superEvento.getAdministradorSuperEventos().stream()
            .collect(java.util.stream.Collectors.groupingBy(a -> a.getUsuario().getUsername()))
            .entrySet().stream()
            .map(entry -> {
                Usuario user = entry.getValue().get(0).getUsuario();
                boolean vigente = entry.getValue().stream().anyMatch(a -> a.getFechaHoraBaja() == null);
                
                List<DTOAdministradoresSuperevento.DTOAdministradores.DTOHistorico> historico = entry.getValue().stream()
                    .map(a -> DTOAdministradoresSuperevento.DTOAdministradores.DTOHistorico.builder()
                        .fechaDesde(TimeUtil.toMillis(a.getFechaHoraAlta()))
                        .fechaHasta(TimeUtil.toMillis(a.getFechaHoraBaja()))
                        .organizador(a.getTipoAdministradorSuperEvento().getNombre().equals("Organizador"))
                        .build())
                    .toList();

                return DTOAdministradoresSuperevento.DTOAdministradores.builder()
                    .nombre(user.getNombre())
                    .apellido(user.getApellido())
                    .username(user.getUsername())
                    .vigente(vigente)
                    .historico(historico)
                    .build();
            }).toList();

        return DTOAdministradoresSuperevento.builder()
            .esOrganizador(esOrganizador)
            .nombreSuperevento(superEvento.getNombre())
            .administradores(administradores)
            .build();
    }

    @Override
    public void agregarAdministrador(Long idSuperevento, String usernameNuevoAdmin) throws Exception {
        SuperEvento superEvento = repo.findById(idSuperevento).orElseThrow(() -> new Exception("No se encontró el superevento"));

        if (superEvento.getFechaHoraBaja() != null) {
            throw new Exception("No se encontró el superevento");
        }

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        boolean esOrganizador = superEvento.getAdministradorSuperEventos().stream()
            .filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorSuperEvento().getNombre().equals("Organizador") && a.getUsuario().getUsername().equals(username))
            .count() > 0;

        if (!esOrganizador) {
            throw new Exception("Debe ser el organizador del superevento para gestionar sus administradores");
        }

        Usuario nuevoAdmin = usuarioRepo.findByUsername(usernameNuevoAdmin).orElseThrow(() -> new Exception("No se encontró al usuario"));

        TipoAdministradorSuperEvento tipoAdmin = tipoAdminRepo.findByNombreIgnoreCase("Administrador")
            .orElseThrow(() -> new Exception("No se pudo asignar el tipo de administrador"));

        boolean yaEsAdmin = superEvento.getAdministradorSuperEventos().stream()
            .anyMatch(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(usernameNuevoAdmin));

        if (yaEsAdmin) {
            throw new Exception("El usuario ya es administrador del superevento");
        }

        AdministradorSuperEvento admin = AdministradorSuperEvento.builder()
            .usuario(nuevoAdmin)
            .superEvento(superEvento)
            .tipoAdministradorSuperEvento(tipoAdmin)
            .fechaHoraAlta(LocalDateTime.now())
            .build();

        adminRepo.save(admin);

        registroSingleton.write("Eventos", "administrador_superevento", "creacion", "Usuario '" + usernameNuevoAdmin + "' agregado como administrador del superevento de ID " + superEvento.getId() + " nombre '" + superEvento.getNombre() + "'");
    }

    @Override
    public void quitarAdministrador(Long idSuperevento, String usernameAdminAQuitar) throws Exception {
        SuperEvento superEvento = repo.findById(idSuperevento).orElseThrow(() -> new Exception("No se encontró el superevento"));

        if (superEvento.getFechaHoraBaja() != null) {
            throw new Exception("No se encontró el superevento");
        }

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        boolean esOrganizador = superEvento.getAdministradorSuperEventos().stream()
            .filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorSuperEvento().getNombre().equals("Organizador") && a.getUsuario().getUsername().equals(username))
            .count() > 0;

        if (!esOrganizador) {
            throw new Exception("Debe ser el organizador del superevento para gestionar sus administradores");
        }

        List<AdministradorSuperEvento> admins = superEvento.getAdministradorSuperEventos().stream()
            .filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(usernameAdminAQuitar))
            .toList();

        if (admins.size() == 0) {
            throw new Exception("El usuario no es administrador del superevento");
        }

        AdministradorSuperEvento admin = admins.get(0);

        if (admin.getTipoAdministradorSuperEvento().getNombre().equals("Organizador")) {
            throw new Exception("No puede quitar al organizador. Primero debe transferir el rol de organizador a otro administrador.");
        }

        admin.setFechaHoraBaja(LocalDateTime.now());

        adminRepo.save(admin);

        registroSingleton.write("Eventos", "administrador_superevento", "eliminacion", "Usuario '" + usernameAdminAQuitar + "' removido como administrador del superevento de ID " + superEvento.getId() + " nombre '" + superEvento.getNombre() + "'");
    }

    @Override
    public void entregarOrganizador(Long idSuperevento, String usernameNuevoOrganizador) throws Exception {
        SuperEvento superEvento = repo.findById(idSuperevento).orElseThrow(() -> new Exception("No se encontró el superevento"));

        if (superEvento.getFechaHoraBaja() != null) {
            throw new Exception("No se encontró el superevento");
        }

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        List<AdministradorSuperEvento> organizadores = superEvento.getAdministradorSuperEventos().stream()
            .filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorSuperEvento().getNombre().equals("Organizador") && a.getUsuario().getUsername().equals(username))
            .toList();

        if (organizadores.size() == 0) {
            throw new Exception("Usted no es el organizador de este superevento");
        }

        AdministradorSuperEvento organizadorActual = organizadores.get(0);

        List<AdministradorSuperEvento> nuevosOrganizadores = superEvento.getAdministradorSuperEventos().stream()
            .filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername().equals(usernameNuevoOrganizador))
            .toList();

        if (nuevosOrganizadores.size() == 0) {
            throw new Exception("El usuario al que intenta entregar el rol de organizador no es administrador del superevento");
        }

        AdministradorSuperEvento adminActual = nuevosOrganizadores.get(0);

        if (adminActual.getTipoAdministradorSuperEvento().getNombre().equals("Organizador")) {
            throw new Exception("El usuario ya es el organizador");
        }

        TipoAdministradorSuperEvento tipoOrganizador = tipoAdminRepo.findByNombreIgnoreCase("Organizador")
            .orElseThrow(() -> new Exception("No se encontró el tipo Organizador"));
        TipoAdministradorSuperEvento tipoAdministrador = tipoAdminRepo.findByNombreIgnoreCase("Administrador")
            .orElseThrow(() -> new Exception("No se encontró el tipo Administrador"));

        // Cambiar el organizador
        organizadorActual.setFechaHoraBaja(LocalDateTime.now());

        AdministradorSuperEvento nuevoOrganizador = AdministradorSuperEvento.builder()
            .usuario(adminActual.getUsuario())
            .superEvento(superEvento)
            .tipoAdministradorSuperEvento(tipoOrganizador)
            .fechaHoraAlta(LocalDateTime.now())
            .build();


        // Cambiar el nuevo administrador
        adminActual.setFechaHoraBaja(LocalDateTime.now());

        AdministradorSuperEvento nuevoAdmin = AdministradorSuperEvento.builder()
            .usuario(organizadorActual.getUsuario())
            .superEvento(superEvento)
            .tipoAdministradorSuperEvento(tipoAdministrador)
            .fechaHoraAlta(LocalDateTime.now())
            .build();

        adminRepo.save(organizadorActual);
        adminRepo.save(nuevoOrganizador);
        adminRepo.save(adminActual);
        adminRepo.save(nuevoAdmin);

        registroSingleton.write("Eventos", "administrador_superevento", "modificacion", "Transferencia de rol de organizador del superevento de ID " + superEvento.getId() + " nombre '" + superEvento.getNombre() + "' de @" + username + " a @" + usernameNuevoOrganizador);
    }

    @Override
    public List<DTOBusquedaUsuario> buscarUsuariosNoAdministradores(Long idSuperevento, String texto) throws Exception {
        SuperEvento superEvento = repo.findById(idSuperevento).orElseThrow(() -> new Exception("No se encontró el superevento"));

        if (superEvento.getFechaHoraBaja() != null) {
            throw new Exception("No se encontró el superevento");
        }

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        boolean esOrganizador = superEvento.getAdministradorSuperEventos().stream()
            .filter(a -> a.getFechaHoraBaja() == null && a.getTipoAdministradorSuperEvento().getNombre().equals("Organizador") && a.getUsuario().getUsername().equals(username))
            .count() > 0;

        if (!esOrganizador) {
            throw new Exception("Debe ser el organizador del superevento para gestionar sus administradores");
        }

        // Generación de keywords
        List<String> keywords = Arrays.asList(texto.split("\\s"))
            .stream().filter(k -> k.length() > 2).toList();

        // TODO: Verificar si la búsqueda debe incluir solo usuarios activos o también dados de baja
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
        List<String> usernamesAdmins = superEvento.getAdministradorSuperEventos().stream()
            .filter(a -> a.getFechaHoraBaja() == null)
            .map(a -> a.getUsuario().getUsername())
            .toList();

        return usuarios.stream()
            .filter(u -> !usernamesAdmins.contains(u.getUsername()))
            .map(u -> DTOBusquedaUsuario.builder()
                .username(u.getUsername())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .build())
            .toList();
    }

    
    
}
