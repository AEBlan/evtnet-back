package com.evtnet.evtnetback.Services;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.evtnet.evtnetback.Entities.Registro;
import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.Repositories.RegistroRepository;
import com.evtnet.evtnetback.Repositories.UsuarioRepository;
import com.evtnet.evtnetback.dto.registros.DTOFiltrosRegistro;
import com.evtnet.evtnetback.dto.registros.DTORegistro;
import com.evtnet.evtnetback.dto.registros.DTORegistroMeta;
import com.evtnet.evtnetback.dto.usuarios.DTOBusquedaUsuario;
import com.evtnet.evtnetback.util.CurrentUser;
import com.evtnet.evtnetback.util.RegistroSingleton;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RegistroServiceImpl extends BaseServiceImpl <Registro, Long> implements RegistroService {
    
    private final RegistroRepository repository;
    private final UsuarioRepository usuarioRepository;

    private final RegistroSingleton registroSingleton;

    @PersistenceContext
    private EntityManager entityManager;

    public RegistroServiceImpl(BaseRepository<Registro, Long> baseRepository, RegistroRepository repository, UsuarioRepository usuarioRepository, RegistroSingleton registroSingleton) {
        super(baseRepository);
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.registroSingleton = registroSingleton;
        
    }

    @Override
    public List<DTORegistroMeta> obtenerRegistros() throws Exception {
        String usernameActual = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));

        Usuario actual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        List<String> permisos = actual.getRolesUsuario().stream().map(r -> r.getRol().getRolPermisos().stream().map(p-> p.getPermiso().getNombre()).toList()).flatMap(List::stream).toList();

        List<Registro> registros = repository.findAll();

        List<DTORegistroMeta> ret = new ArrayList<>();

        registros.forEach(r -> {
            String nombre = r.getNombre();

            if (permisos.contains("VisionLog" + nombre)) {
                ret.add(new DTORegistroMeta(nombre, r.getNombreFormateado()));
            }
        });

        return ret;
    }

    @Override
    public DTORegistroMeta obtenerRegistroFormateado(String nombre) throws Exception {
        Optional<Registro> optRegistro = repository.findByNombre(nombre);
        if (!optRegistro.isPresent()) {
            throw new Exception("No se encontró un registro con el nombre '" + nombre + "'");
        }

        Registro registro = optRegistro.get(); 

        return new DTORegistroMeta(registro.getNombre(), registro.getNombreFormateado());
    }

    @Override
    public List<DTORegistro> buscar(String registro, DTOFiltrosRegistro filtros) throws Exception {
        String usernameActual = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));

        Usuario actual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        List<String> permisos = actual.getRolesUsuario().stream().map(r -> r.getRol().getRolPermisos().stream().map(p-> p.getPermiso().getNombre()).toList()).flatMap(List::stream).toList();

        if (!permisos.contains("VisionLog" + registro)) {
            throw new Exception("No tiene permiso para ver este registro");
        }

        return registroSingleton.getReader(registro, filtros.getFechaHoraDesde(), filtros.getFechaHoraHasta())
        .tipos(filtros.getTipos())
        .subtipos(filtros.getSubtipos())
        .usuarios(filtros.getUsuarios())
        .read();
        
    }

    @Override
    public List<String> obtenerTipos(String registro) throws Exception {
        return repository.obtenerTipos(registro);
    }

    @Override
    public List<String> obtenerSubtipos(String registro) throws Exception {
        return repository.obtenerSubtipos(registro);
    }

    @Override
    public List<DTOBusquedaUsuario> buscarUsuarios(String texto) throws Exception {

        List<String> keywords = Arrays.asList(texto.split("\s"));

        String jpql = "SELECT DISTINCT u FROM Usuario u WHERE 1=1";

        for (int i = 0; i < keywords.size(); i++) {
            jpql += " AND (" + 
            "LOWER (u.mail) LIKE LOWER(:kw" + i + ") OR " + 
            "LOWER (u.username) LIKE LOWER(:kw" + i + ") OR " + 
            "LOWER (TRIM(u.nombre)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%')) OR " + 
            "LOWER (TRIM(u.apellido)) LIKE LOWER(CONCAT('%', TRIM(:kw" + i + "), '%'))" + 
            ")";
        }

        TypedQuery<Usuario> query = entityManager.createQuery(jpql, Usuario.class);

        for (int i = 0; i < keywords.size(); i++) {
            query.setParameter("kw" + i, keywords.get(i));
        }

        List<Usuario> usuarios = query.getResultList();

        return usuarios.stream().map(u -> DTOBusquedaUsuario.builder()
            .username(u.getUsername())
            .mail(u.getMail())
            .nombre(u.getNombre())
            .apellido(u.getApellido())
            .dni(u.getDni())
            //.fechaNacimiento(u.getFechaNacimiento().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            .build()).toList();
        
    }
    
}
