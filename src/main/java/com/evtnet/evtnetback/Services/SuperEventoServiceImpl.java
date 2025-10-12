package com.evtnet.evtnetback.Services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.evtnet.evtnetback.dto.supereventos.DTOBusquedaAdministrados;
import com.evtnet.evtnetback.dto.supereventos.DTOBusquedaMisSuperEventos;
import com.evtnet.evtnetback.dto.supereventos.DTOCrearSuperEvento;
import com.evtnet.evtnetback.dto.supereventos.DTOResultadoBusquedaMisSuperEventos;
import com.evtnet.evtnetback.dto.supereventos.DTOSuperEvento;
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

    @PersistenceContext
    private EntityManager entityManager;

    public SuperEventoServiceImpl(
        BaseRepository<SuperEvento, Long> baseRepository, 
        SuperEventoRepository repo, 
        RegistroSingleton registroSingleton, 
        UsuarioRepository usuarioRepo, 
        AdministradorSuperEventoRepository adminRepo,
        TipoAdministradorSuperEventoRepository tipoAdminRepo
    ) {
        super(baseRepository);
        this.repo = repo;
        this.registroSingleton = registroSingleton;
        this.usuarioRepo = usuarioRepo;
        this.adminRepo = adminRepo;
        this.tipoAdminRepo = tipoAdminRepo;
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

        String jpql = "SELECT DISTINCT s FROM SuperEvento s JOIN s.administradorSuperEventos a WHERE 1=1";

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

                if (historico.get(0).getEstadoEvento().getNombre() == "Cancelado") {
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

        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Sesión no iniciada"));

        return DTOSuperEvento.builder()
            .nombre(superEvento.getNombre())
            .descripcion(superEvento.getDescripcion())
            .esAdministrador(superEvento.getAdministradorSuperEventos().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername() == username).count() >= 0)
            .eventos(superEvento.getEventos().stream().map(e -> {
                List<EventoEstado> historico = e.getEventosEstado().stream().filter(h -> h.getFechaHoraBaja() == null).toList();
                boolean cancelado = false;
                if (historico.size() > 0 || historico.get(0).getEstadoEvento().getNombre() == "Cancelado") {
                    cancelado = true;
                }

                return DTOSuperEvento.DTOEvento.builder()
                    .id(e.getId())
                    .nombre(e.getNombre())
                    .fechaDesde(TimeUtil.toMillis(e.getFechaHoraInicio()))
                    .fechaHasta(TimeUtil.toMillis(e.getFechaHoraFin()))
                    .nombreEspacio(e.getSubEspacio().getEspacio().getNombre())
                    .esAdministrador(e.getAdministradoresEvento().stream().filter(a -> a.getFechaHoraBaja() == null && a.getUsuario().getUsername() == username).count() >= 0)
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
    
}
