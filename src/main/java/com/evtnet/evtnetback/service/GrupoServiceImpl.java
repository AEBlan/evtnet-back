package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.util.CurrentUser;
import com.evtnet.evtnetback.dto.grupos.*;
import com.evtnet.evtnetback.entity.Chat;
import com.evtnet.evtnetback.entity.Grupo;
import com.evtnet.evtnetback.entity.TipoUsuarioGrupo;
import com.evtnet.evtnetback.entity.Usuario;
import com.evtnet.evtnetback.entity.UsuarioGrupo;
import com.evtnet.evtnetback.repository.*;

import com.evtnet.evtnetback.util.RegistroSingleton;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Service
public class GrupoServiceImpl extends BaseServiceImpl <Grupo, Long> implements GrupoService {

    private final GrupoRepository grupoRepo;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioGrupoRepository usuarioGrupoRepository;
    private final ChatRepository chatRepository;
    private final TipoUsuarioGrupoRepository tipoUsuarioGrupoRepository;
    private final MailService mailService;
    private final RegistroSingleton registroSingleton;


    public GrupoServiceImpl(BaseRepository<Grupo, Long> baseRepository,GrupoRepository grupoRepo, UsuarioRepository usuarioRepository, UsuarioGrupoRepository usuarioGrupoRepository, ChatRepository chatRepository, TipoUsuarioGrupoRepository tipoUsuarioGrupoRepository, MailService mailService, RegistroSingleton registroSingleton) {
        super(baseRepository);
        this.grupoRepo = grupoRepo;
        this.usuarioRepository = usuarioRepository;
        this.usuarioGrupoRepository = usuarioGrupoRepository;
        this.chatRepository = chatRepository;
        this.tipoUsuarioGrupoRepository = tipoUsuarioGrupoRepository;
        this.mailService = mailService;
        this.registroSingleton = registroSingleton;
    }

    @Override
    public Page<DTOGrupoSimple> obtenerGrupos(String texto, int page) {
        var pageable = PageRequest.of(page, 10); // tamaño fijo de 10 por ejemplo
        var grupos = grupoRepo.buscarPorTexto(texto, pageable);

        return grupos.map(g -> DTOGrupoSimple.builder()
                .id(g.getId())
                .nombre(g.getNombre())
                .descripcion(g.getDescripcion())
                .fechaAlta(g.getFechaHoraAlta())
                .fechaBaja(g.getFechaHoraBaja())
                .creador(DTOGrupoSimple.CreadorDTO.builder()
                        .nombre(g.getUsuariosGrupo().isEmpty() ? "?" : g.getUsuariosGrupo().get(0).getUsuario().getNombre())
                        .apellido(g.getUsuariosGrupo().isEmpty() ? "" : g.getUsuariosGrupo().get(0).getUsuario().getApellido())
                        .build())
                .build());
    }
    @Override
    public DTOAdminGrupo adminObtenerGrupo(Long id) {
        Grupo g = grupoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        // Buscar creador: el primer admin del grupo
        var adminUG = g.getUsuariosGrupo().stream()
                .filter(ug -> ug.getTipoUsuarioGrupo().getNombre().equalsIgnoreCase("Administrador"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Grupo sin administrador"));

        var creadorDTO = DTOAdminGrupo.CreadorDTO.builder()
                .nombre(adminUG.getUsuario().getNombre())
                .apellido(adminUG.getUsuario().getApellido())
                .username(adminUG.getUsuario().getUsername())
                .mail(adminUG.getUsuario().getMail())
                .build();

        var miembrosDTO = g.getUsuariosGrupo().stream()
                .map(ug -> DTOAdminGrupo.MiembroDTO.builder()
                        .nombre(ug.getUsuario().getNombre())
                        .apellido(ug.getUsuario().getApellido())
                        .username(ug.getUsuario().getUsername())
                        .mail(ug.getUsuario().getMail())
                        .tipo(ug.getTipoUsuarioGrupo().getNombre())
                        .build())
                .collect(Collectors.toList());

        return DTOAdminGrupo.builder()
                .id(g.getId())
                .nombre(g.getNombre())
                .descripcion(g.getDescripcion())
                .creador(creadorDTO)
                .miembros(miembrosDTO)
                .build();
    }

    @Override
    public List<DTOGrupoMisGrupos> obtenerMisGrupos() {
        String username = CurrentUser.getUsername()
                .orElseThrow(() -> new RuntimeException("No hay usuario autenticado"));

        // buscar usuario por username
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Grupo> grupos = grupoRepo.findGruposByUsuario(usuario.getId());

        return grupos.stream()
                .map(g -> DTOGrupoMisGrupos.builder()
                        .id(g.getId())
                        .nombre(g.getNombre())
                        .idChat(g.getChat() != null ? g.getChat().getId() : null)
                        .aceptado(g.getUsuariosGrupo().stream().filter(ug -> ug.getFechaHoraBaja() == null && ug.getUsuario().getUsername().equals(username)).max(Comparator.comparing(UsuarioGrupo::getId)).get().getAceptado())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public DTOGrupo obtenerGrupo(Long id) throws Exception {
        String username = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));

        Grupo g = grupoRepo.findById(id)
                .orElseThrow(() -> new Exception("Grupo no encontrado"));

        // Verificar que el usuario pertenezca (activo o histórico) si querés restringir:
        UsuarioGrupo usuarioGrupo = usuarioGrupoRepository.findActivo(id, username)
                .orElseThrow(() -> new Exception("No pertenece al grupo o está dado de baja"));

        boolean esAdmin = usuarioGrupoRepository.esAdmin(id, username);

        // Participantes activos (solo los que no tienen fechaHoraBaja)
        List<UsuarioGrupo> activos = usuarioGrupoRepository.miembrosActivos(id);

        if (!esAdmin) activos = activos.stream().filter(ug -> ug.getAceptado() != null && ug.getAceptado()).toList();

        List<DTOGrupo.Participante> participantes = activos.stream().map(ug -> {
            LocalDateTime primera = usuarioGrupoRepository.primeraUnion(id, ug.getUsuario().getId());
            return DTOGrupo.Participante.builder()
                    .username(ug.getUsuario().getUsername())
                    .nombre(ug.getUsuario().getNombre())
                    .apellido(ug.getUsuario().getApellido())
                    .fechaHoraUnion(primera != null ? primera : ug.getFechaHoraAlta())
                    .aceptado(ug.getAceptado())
                    .build();
        }).toList();

        return DTOGrupo.builder()
                .id(g.getId())
                .nombre(g.getNombre())
                .descripcion(g.getDescripcion())
                .idChat(g.getChat() != null ? g.getChat().getId() : null)
                .participantes(participantes)
                .esAdministrador(esAdmin)
                .fechaAlta(g.getFechaHoraAlta())
                .invitado(usuarioGrupo.getAceptado() == null || !usuarioGrupo.getAceptado())
                .build();
    }

    @Override
    @Transactional
    public void salir(Long grupoId) throws Exception {
        String username = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));

        Long usuarioId = usuarioRepository.findByUsername(username)
                .map(Usuario::getId)
                .orElseThrow(() -> new Exception("Usuario autenticado no existe"));

        UsuarioGrupo ug = usuarioGrupoRepository
                .findByGrupo_IdAndUsuario_IdAndFechaHoraBajaIsNull(grupoId, usuarioId)
                .orElseThrow(() -> new Exception("No pertenece al grupo o ya está dado de baja"));

        ug.setFechaHoraBaja(LocalDateTime.now());
        usuarioGrupoRepository.save(ug);

        registroSingleton.write("UsuariosGrupos", "usuario_grupo", "eliminacion", "El usuario salió del grupo de ID " + ug.getGrupo().getId() + ", nombre'" + ug.getGrupo().getNombre() + "'");
    }

    @Override
    @Transactional
    public List<DTOBusquedaUsuario> buscarUsuariosParaAgregar(Long idGrupo, String texto) throws Exception {
        String currentUser = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));

        List<Usuario> usuarios = usuarioRepository.buscarUsuariosParaAgregar(idGrupo, texto, currentUser);

        return usuarios.stream()
                .map(u -> DTOBusquedaUsuario.builder()
                        .username(u.getUsername())
                        .nombre(u.getNombre())
                        .apellido(u.getApellido())
                        .fotoPerfil(u.getFotoPerfil())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public DTORespuestaCrearGrupo crearGrupo(DTOCrearGrupo dto) throws Exception {
        String usernameActual = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));

        Usuario creador = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // Crear Chat
        Chat chat = Chat.builder()
                .tipo(Chat.Tipo.GRUPAL) // o DIRECTO/GRUPAL según tu enum
                .fechaHoraAlta(LocalDateTime.now())
                .build();
        chatRepository.save(chat);

        // Crear Grupo
        Grupo grupo = Grupo.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .chat(chat)
                .fechaHoraAlta(LocalDateTime.now())
                .build();
        grupo = grupoRepo.save(grupo);
        registroSingleton.write("UsuariosGrupos", "grupo", "creacion", "Grupo de ID " + grupo.getId() + ", nombre'" + grupo.getNombre() + "'");


        // Agregar participantes
        if (dto.getParticipantes() != null) {
            for (DTOCrearGrupo.Participante p : dto.getParticipantes()) {
                Usuario usuario = usuarioRepository.findByUsername(p.getUsername())
                        .orElseThrow(() -> new Exception("Usuario no encontrado: " + p.getUsername()));

                TipoUsuarioGrupo tipo = tipoUsuarioGrupoRepository.findById(p.getTipo())
                        .orElseThrow(() -> new Exception("Tipo de usuario no válido: " + p.getTipo()));

                UsuarioGrupo ug = UsuarioGrupo.builder()
                        .usuario(usuario)
                        .grupo(grupo)
                        .aceptado(false)
                        .tipoUsuarioGrupo(tipo)
                        .fechaHoraAlta(LocalDateTime.now())
                        .build();

                usuarioGrupoRepository.save(ug);
                registroSingleton.write("UsuariosGrupos", "usuario_grupo", "creacion", "Usuario " + usuario.getUsername() + " invitado al grupo de ID " + ug.getGrupo().getId() + ", nombre'" + ug.getGrupo().getNombre() + "'");

                invitarUsuario(usuario, grupo);
            }
        }

        // Asegurar que el creador esté como administrador
        boolean creadorIncluido = dto.getParticipantes() != null &&
                dto.getParticipantes().stream().anyMatch(p -> p.getUsername().equals(usernameActual));

        if (!creadorIncluido) {
            TipoUsuarioGrupo admin = tipoUsuarioGrupoRepository.findByNombreIgnoreCase("Administrador")
                    .orElseThrow(() -> new Exception("TipoUsuarioGrupo 'Administrador' no existe"));

            UsuarioGrupo ug = UsuarioGrupo.builder()
                    .usuario(creador)
                    .grupo(grupo)
                    .tipoUsuarioGrupo(admin)
                    .fechaHoraAlta(LocalDateTime.now())
                    .aceptado(true)
                    .build();

            usuarioGrupoRepository.save(ug);
        }

        return DTORespuestaCrearGrupo.builder()
                .id(grupo.getId())
                .build();
    }


    @Override
    public void toggleInvitacion(Long idGrupo, Boolean aceptar) throws Exception {
        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Inicie sesión para poder hacer esto"));
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(() -> new Exception("Inicie sesión para poder hacer esto"));

        Grupo grupo = grupoRepo.findById(idGrupo).orElseThrow(() -> new Exception("No se encontró el grupo"));

        UsuarioGrupo usuarioGrupo = grupo.getUsuariosGrupo().stream().filter(ug -> ug.getUsuario().getUsername().equals(username) && (ug.getAceptado() == null || !ug.getAceptado()) && ug.getFechaHoraBaja() == null).max(Comparator.comparing(UsuarioGrupo::getId)).orElseThrow(() -> new Exception("No tiene una invitación pendiente para este grupo"));

        if (aceptar) {
            usuarioGrupo.setAceptado(true);
            usuarioGrupo.setFechaHoraAlta(LocalDateTime.now());
            registroSingleton.write("UsuariosGrupos", "usuario_grupo", "eliminacion", "El usuario aceptó la invitación al grupo de ID " + grupo.getId() + ", nombre'" + grupo.getNombre() + "'");
        } else {
            usuarioGrupo.setFechaHoraBaja(LocalDateTime.now());
            registroSingleton.write("UsuariosGrupos", "usuario_grupo", "eliminacion", "El usuario rechazó la invitación al grupo de ID " + grupo.getId() + ", nombre'" + grupo.getNombre() + "'");
        }

        usuarioGrupoRepository.save(usuarioGrupo);
    }

    @Override
    public List<DTOTipoUsuarioGrupo> obtenerTiposUsuarioGrupo() {
        return tipoUsuarioGrupoRepository.findAll()
                .stream()
                .map(t -> DTOTipoUsuarioGrupo.builder()
                        .id(t.getId())
                        .nombre(t.getNombre())
                        .build())
                .toList();
    }

    // --- GET /grupos/obtenerDatosModificarGrupo?idGrupo=...
    @Override
    @Transactional
    public DTOModificarGrupo obtenerDatosModificarGrupo(Long idGrupo) throws Exception {
        String username = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));

        Grupo g = grupoRepo.findById(idGrupo)
                .orElseThrow(() -> new Exception("Grupo no encontrado"));

        // solo admin puede modificar
        if (!usuarioGrupoRepository.esAdmin(idGrupo, username)) {
            throw new Exception("No posee permisos para modificar este grupo");
        }

        // miembros activos
        List<UsuarioGrupo> activos = usuarioGrupoRepository.miembrosActivosConTodo(idGrupo);

        List<DTOModificarGrupo.Participante> participantes = activos.stream()
                // la pantalla no debe mostrar al usuario logueado
                .filter(ug -> !username.equalsIgnoreCase(ug.getUsuario().getUsername()))
                .map(ug -> DTOModificarGrupo.Participante.builder()
                        .username(ug.getUsuario().getUsername())
                        .nombre(ug.getUsuario().getNombre())
                        .apellido(ug.getUsuario().getApellido())
                        .tipo(ug.getTipoUsuarioGrupo().getId())
                        .build())
                .toList();

        return DTOModificarGrupo.builder()
                .id(g.getId())
                .nombre(g.getNombre())
                .descripcion(g.getDescripcion())
                .participantes(participantes)
                .build();
    }

    // --- PUT /grupos/modificarGrupo
    @Override
    @Transactional
    public void modificarGrupo(DTOModificarGrupo dto) throws Exception {
        String username = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));

        if (dto == null || dto.getId() == null) throw new Exception("Solicitud inválida");
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty())
            throw new Exception("Es obligatorio ingresar un nombre para el grupo");

        Grupo g = grupoRepo.findById(dto.getId())
                .orElseThrow(() -> new Exception("Grupo no encontrado"));

        // autorización: sólo admin
        if (!usuarioGrupoRepository.esAdmin(g.getId(), username)) {
            throw new Exception("No posee permisos para modificar este grupo");
        }

        // actualizar cabecera
        g.setNombre(dto.getNombre().trim());
        g.setDescripcion(dto.getDescripcion() == null ? null : dto.getDescripcion().trim());
        grupoRepo.save(g);

        registroSingleton.write("UsuariosGrupos", "grupo", "modificacion", "Grupo de ID " + g.getId() + ", nombre'" + g.getNombre() + "' modificado");

        // -------- sincronización de miembros ----------
        // participantes deseados (no incluye al usuario logueado)
        List<DTOModificarGrupo.Participante> deseados =
                Optional.ofNullable(dto.getParticipantes()).orElseGet(List::of);

        // validación: debe quedar al menos 1 miembro más (además del admin actual)
        if (deseados.isEmpty())
            throw new Exception("Debe haber al menos un miembro más en el grupo");

        // map de username(lower) -> participante
        Map<String, DTOModificarGrupo.Participante> deseadosPorUsernameLower =
                deseados.stream()
                        .collect(Collectors.toMap(
                                p -> p.getUsername().toLowerCase(Locale.ROOT),
                                Function.identity(),
                                (a, b) -> a));

        // cargar usuarios por username (en lote)
        List<String> usernamesLower = new ArrayList<>(deseadosPorUsernameLower.keySet());
        List<Usuario> usuariosDeseados = usuarioRepository.findAllByUsernameInLower(usernamesLower);
        Map<String, Usuario> usuarioPorUsernameLower =
                usuariosDeseados.stream()
                        .collect(Collectors.toMap(u -> u.getUsername().toLowerCase(Locale.ROOT), Function.identity()));

        // tipos en lote
        List<Long> idsTipos = deseados.stream()
                .map(DTOModificarGrupo.Participante::getTipo)
                .filter(Objects::nonNull).distinct().toList();
        Map<Long, TipoUsuarioGrupo> tipoPorId =
                tipoUsuarioGrupoRepository.findByIdIn(idsTipos).stream()
                        .collect(Collectors.toMap(TipoUsuarioGrupo::getId, Function.identity()));

        // actuales activos (con todo)
        List<UsuarioGrupo> actuales = usuarioGrupoRepository.miembrosActivosConTodo(g.getId());

        LocalDateTime ahora = LocalDateTime.now();

        // 1) dar de baja los que ya no están (excepto el logueado)
        for (UsuarioGrupo ug : actuales) {
            String uLower = ug.getUsuario().getUsername().toLowerCase(Locale.ROOT);
            if (username.equalsIgnoreCase(ug.getUsuario().getUsername())) continue; // no tocar al que modifica
            if (!deseadosPorUsernameLower.containsKey(uLower)) {
                ug.setFechaHoraBaja(ahora);
                usuarioGrupoRepository.save(ug);
                registroSingleton.write("UsuariosGrupos", "usuario_grupo", "eliminacion", "Usuario " + ug.getUsuario().getUsername() + " eliminado del grupo de ID " + ug.getGrupo().getId() + ", nombre'" + ug.getGrupo().getNombre() + "'");
            }
        }

        // 2) crear o cambiar tipo para los deseados
        for (DTOModificarGrupo.Participante p : deseados) {
            String uLower = p.getUsername().toLowerCase(Locale.ROOT);
            Usuario usuario = usuarioPorUsernameLower.get(uLower);
            if (usuario == null) {
                throw new Exception("Usuario inexistente: " + p.getUsername());
            }
            TipoUsuarioGrupo tipo = tipoPorId.get(p.getTipo());
            if (tipo == null) {
                throw new Exception("Tipo de usuario de grupo inválido: " + p.getTipo());
            }

            // buscar si ya estaba activo
            Optional<UsuarioGrupo> existenteOpt = actuales.stream()
                    .filter(ug -> ug.getUsuario().getId().equals(usuario.getId()))
                    .findFirst();

            if (existenteOpt.isEmpty()) {
                // no estaba -> crear
                UsuarioGrupo nuevo = UsuarioGrupo.builder()
                        .usuario(usuario)
                        .grupo(g)
                        .tipoUsuarioGrupo(tipo)
                        .fechaHoraAlta(ahora)
                        .aceptado(false)
                        .build();
                usuarioGrupoRepository.save(nuevo);
                registroSingleton.write("UsuariosGrupos", "usuario_grupo", "creacion", "Usuario " + nuevo.getUsuario().getUsername() + " invitado al grupo de ID " + g.getId() + ", nombre'" + g.getNombre() + "'");

                invitarUsuario(usuario, g);
            } else {
                UsuarioGrupo existente = existenteOpt.get();
                // si cambió el tipo -> cerrar vínculo y crear uno nuevo
                if (!existente.getTipoUsuarioGrupo().getId().equals(tipo.getId())) {
                    existente.setFechaHoraBaja(ahora);
                    usuarioGrupoRepository.save(existente);

                    UsuarioGrupo nuevo = UsuarioGrupo.builder()
                            .usuario(usuario)
                            .grupo(g)
                            .tipoUsuarioGrupo(tipo)
                            .fechaHoraAlta(ahora)
                            .aceptado(existente.getAceptado())
                            .build();
                    usuarioGrupoRepository.save(nuevo);

                    registroSingleton.write("UsuariosGrupos", "usuario_grupo", "modificacion", "Tipo del usuario " + usuario.getUsername() + " del grupo de ID " + g.getId() + ", nombre'" + g.getNombre() + "' modificado");
                }
                // si es el mismo tipo, no hacemos nada
            }
        }
    }

    private void invitarUsuario(Usuario usuario, Grupo grupo) {
        String subject = "evtnet - Invitación a grupo";
        String body = """
            Has sido invitado a unirte al grupo %s en evtnet.
            
            Para aceptar o rechazar la invitación, iniciá sesión y dirigite a la página Mi Perfil &gt Ver Mis Grupos.
        """.formatted(grupo.getNombre());

        mailService.enviar(usuario.getMail(), subject, body);
    }

}
