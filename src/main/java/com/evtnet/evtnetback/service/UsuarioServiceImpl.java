package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.*;
import com.evtnet.evtnetback.repository.*;
import com.evtnet.evtnetback.dto.usuarios.*;
import com.evtnet.evtnetback.security.JwtUtil;
import com.evtnet.evtnetback.util.CurrentUser;
import com.evtnet.evtnetback.util.MercadoPagoSingleton;
import com.evtnet.evtnetback.util.RegistroSingleton;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.extern.slf4j.Slf4j;
import com.evtnet.evtnetback.repository.BaseRepository;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service 
@Slf4j
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario, Long> implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MailService mailService; // inyectado para enviar el mail
    private final CalificacionRepository calificacionRepository;
    private final CalificacionTipoRepository calificacionTipoRepository;
    private final MotivoCalificacionRepository motivoCalificacionRepository;
    private final CalificacionMotivoCalificacionRepository calificacionMotivoCalificacionRepository;
    private final TipoCalificacionRepository tipoCalificacionRepository;
    private final PermisoRepository permisoRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final UsuarioGrupoRepository usuarioGrupoRepository;
    private final EventoRepository eventoRepository;
    private final EspacioRepository espacioRepository;
    private final SuperEventoRepository superEventoRepository;
    private final ChatRepository chatRepository;
    private final DenunciaEventoRepository denunciaEventoRepository;
    private final RegistroSingleton registroSingleton;
    private final ParametroSistemaService parametroSistemaService;

    private final MercadoPagoSingleton mercadoPagoSingleton;
    
    //private final DenunciaEventoEstadoRepository denunciaEventoEstadoRepository;
    //private final EstadoDenunciaEventoRepository estadoDenunciaEventoRepository;


    // Directorio para fotos de perfil (montá un volumen)
    @Value("${app.storage.perfiles:/app/storage/perfiles}")
    private String perfilesDir;

    // Google Sign-In (opcional)
    @Value("${app.google.clientId:}")
    private String googleClientId;

    @Value("${app.storage.tipoCalificacion:/app/storage/tipoCalificacion}")
    private String calificacionesDir;

    @Value("${app.front.resetBaseUrl:http://localhost:5173/usuarios/restablecerContrasena}")
    private String resetBaseUrl;

    public UsuarioServiceImpl(
            BaseRepository<Usuario, Long> baseRepository,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            RolUsuarioRepository rolUsuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            MailService mailService,
            CalificacionRepository calificacionRepository,
            CalificacionTipoRepository calificacionTipoRepository,
            MotivoCalificacionRepository motivoCalificacionRepository,
            CalificacionMotivoCalificacionRepository calificacionMotivoCalificacionRepository,
            TipoCalificacionRepository tipoCalificacionRepository,
            PermisoRepository permisoRepository,
            RolPermisoRepository rolPermisoRepository,
            UsuarioGrupoRepository usuarioGrupoRepository,
            EventoRepository eventoRepository,
            EspacioRepository espacioRepository,
            SuperEventoRepository superEventoRepository,
            ChatRepository chatRepository,
            DenunciaEventoRepository denunciaEventoRepository,
            RegistroSingleton registroSingleton,
            ParametroSistemaService parametroSistemaService,
            MercadoPagoSingleton mercadoPagoSingleton
            //DenunciaEventoEstadoRepository denunciaEventoEstadoRepository,
            //EstadoDenunciaEventoRepository estadoDenunciaEventoRepository
            
) {
        super(baseRepository);
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.mailService = mailService;
        this.calificacionRepository = calificacionRepository;
        this.calificacionTipoRepository = calificacionTipoRepository;
        this.motivoCalificacionRepository = motivoCalificacionRepository;
        this.calificacionMotivoCalificacionRepository = calificacionMotivoCalificacionRepository;
        this.tipoCalificacionRepository = tipoCalificacionRepository;
        this.permisoRepository = permisoRepository;
        this.rolPermisoRepository = rolPermisoRepository;
        this.usuarioGrupoRepository = usuarioGrupoRepository;
        this.eventoRepository = eventoRepository;
        this.espacioRepository = espacioRepository;
        this.superEventoRepository = superEventoRepository;
        this.chatRepository = chatRepository;
        this.denunciaEventoRepository = denunciaEventoRepository;
        this.registroSingleton = registroSingleton;
        this.parametroSistemaService = parametroSistemaService;
        this.mercadoPagoSingleton = mercadoPagoSingleton;
        //this.denunciaEventoEstadoRepository = denunciaEventoEstadoRepository;
        //this.estadoDenunciaEventoRepository = estadoDenunciaEventoRepository;

}

    // ===== Config (recupero por mail) =====
    private static final Duration RESET_TTL = Duration.ofMinutes(15);         // vence a los 15 min
    private static final Duration RESEND_MIN_INTERVAL = Duration.ofSeconds(45);
    private static final int MAX_PER_HOUR = 5;
    private static final boolean PRIVACY_MODE = true;                         // no revelar si existe el mail

    // ===== Estado en memoria (thread-safe) =====
    private static final class CodeMeta {
        final String code;
        final Instant expiresAt;
        Instant lastSentAt;
        int sentThisHour;
        Instant hourWindowStart;

        CodeMeta(String code, Instant now) {
            this.code = code;
            this.expiresAt = now.plus(RESET_TTL);
            this.lastSentAt = now;
            this.hourWindowStart = now;
            this.sentThisHour = 1;
        }
        boolean expired(Instant now) { return now.isAfter(expiresAt); }
        boolean canResend(Instant now) {
            if (Duration.between(lastSentAt, now).compareTo(RESEND_MIN_INTERVAL) < 0) return false;
            if (Duration.between(hourWindowStart, now).toHours() >= 1) {
                hourWindowStart = now;
                sentThisHour = 0;
            }
            return sentThisHour < MAX_PER_HOUR;
        }
        void markResent(Instant now) { lastSentAt = now; sentThisHour++; }
    }

    // Recupero contraseña (usa CodeMeta)
    private final Map<String, CodeMeta> codigosRecuperoPorMail = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    // Registro (código simple en memoria)
    private final Map<String, String> codigosRegistroPorMail = new ConcurrentHashMap<>();

    // ===== Utils =====
    private String normalizeMail(String mail) {
        return mail == null ? null : mail.trim().toLowerCase();
    }
    private String generateCode() {
        return String.format("%06d", secureRandom.nextInt(1_000_000)); // 000000..999999
    }

    private LocalDateTime parseFechaNacimiento(String maybeIsoOrMillis) {
        if (maybeIsoOrMillis == null || maybeIsoOrMillis.isBlank()) return null;
        try {
            long ms = Long.parseLong(maybeIsoOrMillis); // millis
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());
        } catch (NumberFormatException ignore) {
            Instant inst = Instant.parse(maybeIsoOrMillis); // ISO-8601
            return LocalDateTime.ofInstant(inst, ZoneId.systemDefault());
        }
    }

    // ================== ENVIAR CÓDIGO (RECUPERO) ==================
    @Override
    public void enviarCodigoRecuperarContrasena(String mail) {
        String m = normalizeMail(mail);
        Instant now = Instant.now();

        boolean existe = usuarioRepository.existsByMail(m);

        // Privacy mode: no revelar si existe
        if (!existe && PRIVACY_MODE) return;
        if (!existe) throw new IllegalArgumentException("Mail no registrado");

        CodeMeta meta = codigosRecuperoPorMail.get(m);
        if (meta == null || meta.expired(now)) {
            meta = new CodeMeta(generateCode(), now);
            codigosRecuperoPorMail.put(m, meta);
        } else {
            if (!meta.canResend(now)) return; // silencio anti-spam
            meta.markResent(now);
        }

        String subject = "Código de recuperación de contraseña";
        String body = """
                Hola,

                Tu código de recuperación es: %s
                Este código expira en %d minutos.

                Si no solicitaste este código, podés ignorar este mensaje.
                """.formatted(meta.code, RESET_TTL.toMinutes());

        mailService.enviar(m, subject, body);
    }


    @Value("${app.frontend.base-url:localhost:5173}")
    private String frontendBaseUrl;

    @Value("${app.frontend.invite-path:/RecuperarContrasena}")
    private String invitePath;

    /**
     * Envía un código de invitación + link para que el usuario cree su contraseña.
     */
    public void enviarCodigoAltaUsuario(String mail) {
        String path= "http://localhost:5173/RecuperarContrasena/";
        String m = normalizeMail(mail);
        Instant now = Instant.now();

        if (!usuarioRepository.existsByMail(m)) {
            throw new IllegalArgumentException("Mail no registrado");
        }

        CodeMeta meta = codigosRecuperoPorMail.get(m);
        if (meta == null || meta.expired(now)) {
            meta = new CodeMeta(generateCode(), now);
            codigosRecuperoPorMail.put(m, meta);
        } else {
            if (!meta.canResend(now)) return; // anti-spam silencioso
            meta.markResent(now);
        }

        // Armamos el link directo con mail y codigo como query params
        String inviteUrl = UriComponentsBuilder
                .fromHttpUrl(frontendBaseUrl)
                .path(invitePath)       // el front lo leerá del query string
                .build()
                .toUriString();

        String subject = "Creá tu contraseña para acceder";
        String body = """
                ¡Hola!

                Tu cuenta fue creada por un administrador.
                Usá este código para crear tu contraseña: %s
                El código expira en %d minutos.

                También podés entrar directamente con este enlace:
                %s%s

                Si no esperabas este correo, podés ignorarlo.
                """.formatted(meta.code, RESET_TTL.toMinutes(), path,m);

        try {
        mailService.enviar(m, subject, body);
        log.info("Email de invitación enviado a {} con código {}", m, meta.code);
        } catch (Exception e) {
            log.error("Fallo al enviar email a {}: {}", m, e.getMessage(), e);
            throw e;
        }
    }

    // ---------- AUTH LOCAL ----------
    private DTOAuth authFromUser(Usuario u) throws Exception {
        List<String> permisos = rolUsuarioRepository.findPermisosByUsername(u.getUsername());
        if (permisos == null) permisos = List.of(); // por las dudas
    
        String token = jwtUtil.generateToken(u.getUsername(), permisos);
        registroSingleton.write("UsuariosGrupos", "inicio_sesion", "creacion", "El usuario se autenticó e inició sesión", u.getUsername());
        return DTOAuth.builder()
                .token(token)
                .permisos(permisos)
                .username(u.getUsername())
                .vinculadoMP(mercadoPagoSingleton.checkUsuarioAutorizado(u))
                .user(DTOAuth.User.builder()
                    .nombre(u.getNombre())
                    .apellido(u.getApellido())
                    .roles(u.getRolesUsuario().stream().map(r -> r.getRol().getNombre()).toList())
                    .build())
                .build();
    }

    @Override
    public DTOAuth login(String mail, String password) throws Exception {
        Usuario u;

        // si contiene "@" lo tratamos como mail
        if (mail.contains("@")) {
            u = usuarioRepository.findByMail(mail)
                    .orElseThrow(() -> new Exception("Usuario no encontrado por mail"));
        } else {
            u = usuarioRepository.findByUsername(mail)
                    .orElseThrow(() -> new Exception("Usuario no encontrado por username"));
        }

        if (!passwordEncoder.matches(password, u.getContrasena())) {
            throw new Exception("Credenciales inválidas");
        }

        return authFromUser(u);
    }

    @Override
    @Transactional
    public DTOAuth register(DTORegistrarse body) throws Exception {
        if (usuarioRepository.existsByMail(body.getMail()))
            throw new Exception("Mail ya registrado");
        if (usuarioRepository.existsByUsername(body.getUsername()))
            throw new Exception("Username no disponible");

        Usuario u = Usuario.builder()
                .username(body.getUsername())
                .mail(body.getMail())
                .nombre(body.getNombre())
                .apellido(body.getApellido())
                .dni(body.getDni())
                .fechaNacimiento(parseFechaNacimiento(body.getFechaNacimiento()))
                .contrasena(passwordEncoder.encode(body.getPassword()))
                .fechaHoraAlta(LocalDateTime.now())
                .rolesUsuario(new ArrayList<>())
                .build();
        u = usuarioRepository.save(u);

        Rol rolPend = rolRepository.findByNombre("PendienteConfirmación")
                .orElseThrow(() -> new IllegalStateException("Falta rol PendienteConfirmación"));
        if (!rolUsuarioRepository.existsByUsuarioAndRol(u, rolPend)) {
            rolUsuarioRepository.save(
                    RolUsuario.builder().usuario(u).rol(rolPend).fechaHoraAlta(LocalDateTime.now()).build()
            );
        }

        u = usuarioRepository.findByUsername(u.getUsername())
            .orElseThrow(() -> new Exception("Usuario no encontrado"));

        enviarCodigo(body.getMail());
        return authFromUser(u);
    }

    @Override
    @Transactional
    public DTOAuth registerConFoto(DTORegistrarse body, byte[] foto, String nombreArchivo, String contentType) throws Exception {
        if (usuarioRepository.existsByMail(body.getMail()))
            throw new Exception("Mail ya registrado");
        if (usuarioRepository.existsByUsername(body.getUsername()))
            throw new Exception("Username no disponible");

        Usuario u = Usuario.builder()
                .username(body.getUsername())
                .mail(body.getMail())
                .nombre(body.getNombre())
                .apellido(body.getApellido())
                .dni(body.getDni())
                .fechaNacimiento(parseFechaNacimiento(body.getFechaNacimiento())) // <<< unificado
                .contrasena(passwordEncoder.encode(body.getPassword()))
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        // 1) Persistir para obtener ID
        u = usuarioRepository.save(u);

        // 2) Guardar foto (si vino)
        if (foto != null && foto.length > 0) {
            Files.createDirectories(Paths.get(perfilesDir)); // ej: ./uploads/perfiles
            String ext = getExtension(nombreArchivo);
            String filename = u.getUsername() + "_" + System.currentTimeMillis() + (ext.isEmpty() ? "" : "." + ext);
            Path destino = Paths.get(perfilesDir).resolve(filename).toAbsolutePath().normalize();
            Files.write(destino, foto, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            u.setFotoPerfil(destino.toString());
            usuarioRepository.save(u);
        }

        // 3) Rol inicial igual que en register(...)
        Rol rolPend = rolRepository.findByNombre("PendienteConfirmación")
                .orElseThrow(() -> new IllegalStateException("Falta rol PendienteConfirmación"));
        if (!rolUsuarioRepository.existsByUsuarioAndRol(u, rolPend)) {
            RolUsuario ru = RolUsuario.builder().usuario(u).rol(rolPend).build();
            ru.setFechaHoraAlta(LocalDateTime.now());
            rolUsuarioRepository.save(ru);
        }

        // 4) Enviar código (igual que antes)
        enviarCodigo(body.getMail());

        // 5) Devolver DTOAuth
        return authFromUser(u);
    }

    // ---------- CÓDIGOS (registro/verificación) ----------
    @Override
    public void enviarCodigo(String mail) throws Exception {
        // 1) Validar que el usuario exista
        usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new Exception("Usuario no encontrado para " + mail));

        // 2) Generar y guardar el código (como ya hacías)
        String code = generateCode();
        codigosRegistroPorMail.put(mail, code);

        // 3) Enviar el correo (igual estilo que recuperación)
        String subject = "evtnet - Código de verificación de cuenta";
        String body = """
                Hola,

                Tu código de verificación es: %s
                Este código expira en %d minutos.

                Si no solicitaste este código, podés ignorar este mensaje.
                """.formatted(code, RESET_TTL.toMinutes()); // podés usar otro TTL si querés

        mailService.enviar(mail, subject, body);
    }

    @Override
    @Transactional
    public DTOAuth ingresarCodigo(String codigo) throws Exception {
        String mail = codigosRegistroPorMail.entrySet().stream()
                .filter(e -> Objects.equals(e.getValue(), codigo))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new Exception("Código inválido"));

        Usuario u = usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        Rol rolPend = rolRepository.findByNombre("PendienteConfirmación")
                .orElseThrow(() -> new IllegalStateException("Falta rol PendienteConfirmación"));
        Rol rolUsr = rolRepository.findByNombre("Usuario")
                .orElseThrow(() -> new IllegalStateException("Falta rol Usuario"));

        rolUsuarioRepository.findByUsuarioAndRol(u, rolPend)
                .ifPresent(ru -> rolUsuarioRepository.deleteByUsuarioAndRol(u, rolPend));
        if (!rolUsuarioRepository.existsByUsuarioAndRol(u, rolUsr)) {
            rolUsuarioRepository.save(
                    RolUsuario.builder()
                            .usuario(u)
                            .rol(rolUsr)
                            .fechaHoraAlta(LocalDateTime.now())
                            .build()
            );
        }

        codigosRegistroPorMail.remove(mail);
        registroSingleton.write("UsuariosGrupos", "usuario", "creacion", "El usuario se registró", u.getUsername());

        return authFromUser(u);
    }

    // ---------- GOOGLE ----------
    @Override
    public DTOAuth loginGoogle(String idToken) throws Exception {
        if (googleClientId == null || googleClientId.isBlank()) {
            throw new IllegalStateException("Falta configurar app.google.clientId");
        }

        var transport = new NetHttpTransport();
        var jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken token = verifier.verify(idToken);
        if (token == null) throw new Exception("ID token inválido");

        GoogleIdToken.Payload payload = token.getPayload();

        String iss = payload.getIssuer();
        if (!"accounts.google.com".equals(iss) && !"https://accounts.google.com".equals(iss)) {
            throw new Exception("Issuer inválido");
        }
        Long expSec = payload.getExpirationTimeSeconds();
        if (expSec == null || expSec * 1000 < System.currentTimeMillis()) {
            throw new Exception("ID token expirado");
        }

        String email = payload.getEmail();
        boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());
        if (!emailVerified) throw new Exception("Email de Google no verificado");

        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        Usuario u = usuarioRepository.findByMail(email).orElseGet(() -> {
            Usuario nu = Usuario.builder()
                    .mail(email)
                    .username(suggestUsername(email))
                    .nombre(name)
                    .contrasena(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .fechaHoraAlta(LocalDateTime.now())
                    .build();
            return usuarioRepository.save(nu);
        });

        Rol rolUsr = rolRepository.findByNombre("Usuario")
                .orElseThrow(() -> new IllegalStateException("Falta rol Usuario"));
        if (!rolUsuarioRepository.existsByUsuarioAndRol(u, rolUsr)) {
            rolUsuarioRepository.save(RolUsuario.builder().usuario(u).rol(rolUsr).build());
        }

        if ((u.getFotoPerfil() == null || u.getFotoPerfil().isBlank()) && pictureUrl != null) {
            u.setFotoPerfil(pictureUrl);
            usuarioRepository.save(u);
        }

        return authFromUser(u);
    }

    @Override
    @Transactional
    public void definirContrasena(String mail, String nuevaPassword) throws Exception {
        Usuario u = usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        u.setContrasena(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(u);
    }

    @Override
    public boolean usernameDisponible(String username) {
        return !usuarioRepository.existsByUsername(username);
    }

    @Override
    public DTOAuth recuperarContrasena(String mail, String password, String codigo) throws Exception {
        String m = normalizeMail(mail);
        CodeMeta meta = codigosRecuperoPorMail.get(m);
        if (meta == null) throw new Exception("Código inválido o expirado");

        Instant now = Instant.now();
        if (meta.expired(now)) {
            codigosRecuperoPorMail.remove(m);
            throw new Exception("Código expirado");
        }
        if (!Objects.equals(meta.code, codigo)) {
            throw new Exception("Código inválido");
        }

        Usuario u = usuarioRepository.findByMail(m)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        u.setContrasena(passwordEncoder.encode(password));
        usuarioRepository.save(u);
        codigosRecuperoPorMail.remove(m); // consumir

        return authFromUser(u);
    }

    @Override
    public void restablecerContrasena(String currentPassword, String newPassword) throws Exception {
        String usernameActual = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));
        Usuario u = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        if (!passwordEncoder.matches(currentPassword, u.getContrasena()))
            throw new Exception("Contraseña actual incorrecta");

        u.setContrasena(passwordEncoder.encode(newPassword));
        usuarioRepository.save(u);
    }

    // ---------- PERFIL ----------
    @Override
    public DTOPerfil obtenerPerfil(String username) throws Exception {
        Usuario u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        Long fnac = (u.getFechaNacimiento() == null) ? null
                : u.getFechaNacimiento().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            // --- Conteo por tipo (Buena/Media/Mala) ---
        List<Object[]> rows = calificacionRepository.conteoPorTipo(username);
        long total = rows.stream()
                .mapToLong(r -> ((Number) r[1]).longValue())
                .sum();

        // armamos siempre los 3 tipos; si total=0 → 0%
        List<DTOPerfil.ItemCalificacion> items = new ArrayList<>();

        List<String> tipos = tipoCalificacionRepository.findAll().stream().filter(ct -> ct.getFechaHoraBaja() == null).map(ct -> ct.getNombre()).toList();

        for (String t : tipos) {
            long cnt = rows.stream()
                    .filter(r -> t.equalsIgnoreCase((String) r[0]))
                    .mapToLong(r -> ((Number) r[1]).longValue())
                    .findFirst()
                    .orElse(0L);

            int pct = (total == 0) ? 0 : (int) Math.round(cnt * 100.0 / total);
            items.add(new DTOPerfil.ItemCalificacion(t, pct));
        }
        // Si preferís mostrar íconos con 0%, en lugar de null, descomentá:
        // if (items == null) items = java.util.List.of(
        //     new DTOPerfil.ItemCalificacion("Buena", 0),
        //     new DTOPerfil.ItemCalificacion("Media", 0),
        //     new DTOPerfil.ItemCalificacion("Mala", 0)
        // );

        boolean mostrarPerfilCompleto = false;

        String currUsername = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe iniciar sesión para ver esto"));
        Usuario currUsuario = usuarioRepository.findByUsername(currUsername).orElseThrow(() -> new Exception("Debe iniciar sesión para ver esto"));

        if (Objects.equals(currUsername, username)) mostrarPerfilCompleto = true;

        if (currUsuario.getPermisos().contains("VisionPerfilTerceroCompleta")) mostrarPerfilCompleto = true;

        Chat chat = chatRepository.findDirectoBetween(currUsername, username).orElse(null);

        return DTOPerfil.builder()
                .username(u.getUsername())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .mail(mostrarPerfilCompleto ? u.getMail() : "")
                .dni(mostrarPerfilCompleto ? u.getDni() : "")
                .fechaNacimiento(mostrarPerfilCompleto ? fnac : null)
                .calificaciones(items)
                .idChat(chat != null ? chat.getId() : null)
                .vinculadoMP(mostrarPerfilCompleto ? mercadoPagoSingleton.checkUsuarioAutorizado(u) : null)
                .build();
    }

    @Override
    public DTOEditarPerfil obtenerPerfilParaEditar(String username) throws Exception {
        Usuario u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        Long fnac = (u.getFechaNacimiento() == null) ? null
                : u.getFechaNacimiento().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        return DTOEditarPerfil.builder()
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .dni(u.getDni())
                .cbu(u.getCBU())
                .fechaNacimiento(fnac)
                .build();
    }

    @Override
    public void editarPerfil(DTOEditarPerfil datos, byte[] foto, String nombreArchivo, String contentType) throws Exception {
        String usernameActual = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));
        Usuario u = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

                log.info("Editando usuario {} -> nombre={}, apellido={}, dni={}, cbu={}, fechaNac={}",
                u.getUsername(),
                datos.getNombre(),
                datos.getApellido(),
                datos.getDni(),
                datos.getCbu(),
                datos.getFechaNacimiento()
        );

        // Datos básicos
        if (datos != null) {
            if (datos.getNombre() != null) u.setNombre(datos.getNombre());
            if (datos.getApellido() != null) u.setApellido(datos.getApellido());
            if (datos.getDni() != null) u.setDni(datos.getDni());
            if (datos.getCbu() != null) u.setCBU(datos.getCbu());
            if (datos.getFechaNacimiento() != null) {
                u.setFechaNacimiento(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(datos.getFechaNacimiento()), ZoneId.systemDefault()));
            }
        }

        // Guardar foto si vino
        if (foto != null && foto.length > 0) {
            Files.createDirectories(Paths.get(perfilesDir)); // mismo que registerConFoto

            String ext = getExtension(nombreArchivo);
            String filename = usernameActual + "_" + System.currentTimeMillis() + (ext.isEmpty() ? "" : "." + ext);

            Path destino = Paths.get(perfilesDir).resolve(filename).toAbsolutePath().normalize();
            Files.write(destino, foto, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // Guardar SOLO el nombre en DB (como en registerConFoto)
            u.setFotoPerfil(filename); // ruta absoluta

        }

        usuarioRepository.save(u);
    }



    @Override
    public FotoResponse obtenerFotoDePerfil(String username) throws Exception {
        var u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        if (u.getFotoPerfil() == null || u.getFotoPerfil().isBlank()) {
            return obtenerFotoDePerfilFallback();
        }

        Path path = Paths.get(perfilesDir).resolve(u.getFotoPerfil()).toAbsolutePath().normalize();
        if (!Files.exists(path)) return obtenerFotoDePerfilFallback();

        String ct = Files.probeContentType(path);
        return new FotoResponse(Files.readAllBytes(path), ct);
    }

    private FotoResponse obtenerFotoDePerfilFallback() throws IOException {
        File file = new File(getClass().getResource("/default.png").getFile());
        Path path = file.toPath();
        return new FotoResponse(Files.readAllBytes(path), Files.probeContentType(path));
    }


    @Override
    public FotoResponseString obtenerImagenDeCalificacion(String nombre) throws Exception {

        TipoCalificacion tc = tipoCalificacionRepository.findByNombreIgnoreCase(nombre).orElseThrow(() -> new Exception("No se encontró el tipo de calificación"));

        Path imagePath = Paths.get(calificacionesDir).resolve(tc.getImagen());

        if (!Files.exists(imagePath)) {
            throw new FileNotFoundException("Imagen no encontrada: " + nombre);
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);
        String asString = new String(imageBytes, StandardCharsets.ISO_8859_1);
        String contentType = Files.probeContentType(imagePath);

        return new FotoResponseString(asString, contentType);
    }

    @Override
    public String obtenerLinkIntegrarMP() throws Exception {
        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe iniciar sesión para realizar esta acción"));
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(() -> new Exception("Debe iniciar sesión para realizar esta acción"));

        String aux = mercadoPagoSingleton.getAuthorizationUrl(username);
        return aux;
    }

    @Override
    public void obtenerCredencialesMP(String code, String state) throws Exception {
        String username = CurrentUser.getUsername().orElseThrow(() -> new Exception("Debe iniciar sesión para realizar esta acción"));
        if (!state.equals(username)) {
            throw new Exception("Solo el titular de la cuenta puede vincularla a Mercado Pago");
        }
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow(() -> new Exception("Debe iniciar sesión para realizar esta acción"));

        MercadoPagoSingleton.OAuthCredentials credenciales = mercadoPagoSingleton.exchangeCodeForToken(code, state);

        usuario.setMercadoPagoUserId(credenciales.userId);
        usuario.setMercadoPagoPublicKey(credenciales.publicKey);
        usuario.setMercadoPagoAccessToken(credenciales.accessToken);
        usuario.setMercadoPagoRefreshToken(credenciales.refreshToken);

        usuarioRepository.save(usuario);
    }

    @Override
    public void cancelarPagoIncompleto(List<DTOPago> pagos) throws Exception {
        mercadoPagoSingleton.refundIncompletePayments(pagos);
    }


    // ---------- Helpers ----------
    private String suggestUsername(String email) {
        String base = email.split("@")[0];
        String candidate = base;
        int i = 1;
        while (usuarioRepository.existsByUsername(candidate)) {
            candidate = base + i++;
        }
        return candidate;
    }

    private String getExtension(String nombreArchivo) {
        if (nombreArchivo == null) return "";
        int i = nombreArchivo.lastIndexOf('.');
        return i >= 0 ? nombreArchivo.substring(i + 1) : "";
    }

    // ================== CALIFICACIONES ==================
    @Override
    @Transactional
    public List<DTOCalificacionTipoSimple> obtenerCalificacionTiposPara(String usernameDestino) throws Exception {
        // Validar usuario destino
        var destino = usuarioRepository.findByUsername(usernameDestino)
                .orElseThrow(() -> new Exception("Usuario destino no encontrado"));

        // Usuario actual (autor de la calificación)
        var autorUsername = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));
        var autor = usuarioRepository.findByUsername(autorUsername)
                .orElseThrow(() -> new Exception("Usuario origen no encontrado"));

        // 1) No te podés calificar a vos mismo
        if (Objects.equals(autor.getId(), destino.getId())) {
            throw new Exception("Solo se puede calificar a otra persona");
        }

        // 2) Traer tipos activos
        var tiposBase = calificacionTipoRepository.findByFechaHoraBajaIsNullOrderByNombreAsc();
        if (tiposBase == null || tiposBase.isEmpty()) return List.of();

        // 3) Si el destino está dado de baja → solo Denuncia
        boolean destinoDadoDeBaja = destino.getFechaHoraBaja() != null;

        List<DTOCalificacionTipoSimple> result = new ArrayList<>();
        for (var t : tiposBase) {
            String nombre = t.getNombre() == null ? "" : t.getNombre().trim();

            if (destinoDadoDeBaja && !nombre.equalsIgnoreCase("Denuncia")) {
                continue;
            }

            result.add(DTOCalificacionTipoSimple.builder()
                    .id(t.getId())
                    .nombre(t.getNombre())
                    .build());
        }

        return result;
    }

    @Override
    public List<DTOTipoCalificacion> obtenerTiposYMotivosCalificacion() throws Exception {
        var tipos = tipoCalificacionRepository.findAllByOrderByNombreAsc();

        return tipos.stream().map(t -> {
            var motivos = (t.getMotivoCalificaciones() == null)
                    ? java.util.List.<com.evtnet.evtnetback.entity.MotivoCalificacion>of()
                    : t.getMotivoCalificaciones();

            var motivosDTO = motivos.stream()
                    // si Base tiene fechaHoraBaja, filtramos así:
                    .map(m -> DTOMotivoCalificacionSimple.builder()
                            .id(m.getId())
                            .nombre(m.getNombre())
                            .build())
                    .toList();

            return DTOTipoCalificacion.builder()
                    .id(t.getId())
                    .nombre(t.getNombre())
                    .motivos(motivosDTO)
                    .build();
        }).toList();
    }

    @Override
    @Transactional
    public void calificarUsuario(DTOCalificacionRequest body) throws Exception {
        if (body == null) throw new IllegalArgumentException("Body requerido");
        if (body.getUsuarioCalificado() == null || body.getUsuarioCalificado().isBlank())
            throw new IllegalArgumentException("usuarioCalificado requerido");
        if (body.getCalificacionTipo() == null)
            throw new IllegalArgumentException("calificacionTipo requerido");

        // 1) Origen y destino
        var destino = usuarioRepository.findByUsername(body.getUsuarioCalificado())
                .orElseThrow(() -> new Exception("Usuario destino no encontrado"));

        var origenUsername = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));
        Usuario origen = usuarioRepository.findByUsername(origenUsername)
                .orElseThrow(() -> new Exception("Usuario origen no encontrado"));

        if (Objects.equals(origen.getId(), destino.getId()))
            throw new IllegalArgumentException("Solo se puede calificar a otra persona");

        // 2) Tipo de calificación (rosa): "Calificacion Normal" | "Calificacion Denuncia"
        var califTipo = calificacionTipoRepository.findById(body.getCalificacionTipo())
                .orElseThrow(() -> new Exception("Tipo de calificación inválido"));


        String nombreTipo = (califTipo.getNombre() == null) ? "" : califTipo.getNombre().trim().toLowerCase();

        // Normalizamos para evitar problemas con tildes
        nombreTipo = nombreTipo.replace("á", "a");

        boolean esDenuncia = nombreTipo.equalsIgnoreCase("Denuncia");

        // Si es una calificación normal, verificar que ha pasado el timeout necesario para calificar
        if (!esDenuncia) {
            Calificacion previa = origen.getCalificacionesAutores().stream().filter(c -> Objects.equals(c.getCalificado().getUsername(), body.getUsuarioCalificado())).max(Comparator.comparing(Calificacion::getFechaHora)).orElse(null);

            if (previa != null) {
                Integer calif_timeout = parametroSistemaService.getInt("calif_timeout", 72);

                LocalDateTime threshold = LocalDateTime.now().minusHours(calif_timeout);
                if (previa.getFechaHora().isAfter(threshold)) {
                    throw new Exception("Ya ha calificado recientemente a este usuario");
                }
            }

        }

        // 3) Crear la calificación base (siempre)
        var cal = new Calificacion();
        cal.setAutor(origen);
        cal.setCalificado(destino);
        cal.setCalificacionTipo(califTipo);
        cal.setDescripcion(body.getDescripcion());
        cal.setFechaHora(java.time.LocalDateTime.now());
        cal = calificacionRepository.save(cal);

        // 4) Bifurcación: si es "Denuncia" → terminamos SIN motivos

        if (esDenuncia) {
            // Nada más que hacer: no hay motivos ni tipo verde
            return;
        }

        // 5) Si es "Normal": opcionalmente vínculos a MotivoCalificacion
        //    (los motivos pertenecen a un TipoCalificacion verde, inferimos ese tipo por los mismos motivos)
        if (body.getMotivos() != null && !body.getMotivos().isEmpty()) {
            // Regla opcional: todos los motivos deberían pertenecer al MISMO TipoCalificacion (verde)
            Long tipoVerdeId = null;

            for (Long motivoId : body.getMotivos()) {
                var motivo = motivoCalificacionRepository.findById(motivoId)
                        .orElseThrow(() -> new Exception("Motivo no encontrado: " + motivoId));

                // Si querés forzar consistencia: todos los motivos del mismo tipo verde
                Long thisTipoVerdeId = motivo.getTipoCalificacion() != null ? motivo.getTipoCalificacion().getId() : null;
                if (tipoVerdeId == null) tipoVerdeId = thisTipoVerdeId;
                else if (!java.util.Objects.equals(tipoVerdeId, thisTipoVerdeId)) {
                    throw new IllegalArgumentException("Todos los motivos deben pertenecer al mismo TipoCalificacion.");
                }

                var link = new CalificacionMotivoCalificacion();
                link.setCalificacion(cal);
                link.setMotivoCalificacion(motivo);
                calificacionMotivoCalificacionRepository.save(link);
            }
        }
    }
    // ================== ROLES ==================
    @Override
    public List<DTORolSimple> obtenerRoles() throws Exception {
        String usernameActual = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));

        Usuario actual = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        List<Rol> roles = rolRepository.findByFechaHoraBajaIsNullOrderByNombreAsc();

        return roles.stream()
                .map(r -> DTORolSimple.builder()
                        .id(r.getId())
                        .nombre(r.getNombre())
                        .checked(
                            actual.getRolesUsuario() != null && actual.getRolesUsuario().stream()
                                    .anyMatch(ru -> ru.getRol().getId().equals(r.getId())
                                            && ru.getFechaHoraBaja() == null)
                        )
                        .build())
                .toList();
    }


    @Override
    public List<DTOPermisoSimple> obtenerPermisos() {
        return permisoRepository.findAllByOrderByNombreAsc().stream()
                .map(p -> DTOPermisoSimple.builder()
                        .nombre(p.getNombre())
                        .reservado(false) // si tu entidad Permiso tiene 'reservado', mapealo aquí
                        .build())
                .toList();
    }

    
    @Override
    public Page<DTORol> obtenerRolesCompletos(Pageable pageable) {
        return rolRepository.findAll(pageable)
                .map(rol -> {
                    List<RolPermiso> rps = rolPermisoRepository.findByRolIdOrderByFechaHoraAltaAsc(rol.getId());

                    Map<String, List<DTORol.Periodo>> porPermiso = new LinkedHashMap<>();
                    for (RolPermiso rp : rps) {
                        String nom = rp.getPermiso().getNombre();
                        porPermiso.computeIfAbsent(nom, k -> new ArrayList<>())
                                .add(DTORol.Periodo.builder()
                                        .desde(rp.getFechaHoraAlta())
                                        .hasta(rp.getFechaHoraBaja())
                                        .build());
                    }

                    List<DTORol.PermisoEnRol> permisosDTO = porPermiso.entrySet().stream()
                            .map(e -> DTORol.PermisoEnRol.builder()
                                    .nombre(e.getKey())
                                    .reservado(false) // si tu Permiso tiene 'reservado', mapealo aquí
                                    .periodos(e.getValue())
                                    .build())
                            .toList();

                    return DTORol.builder()
                            .id(rol.getId())
                            .nombre(rol.getNombre())
                            .descripcion(rol.getDescripcion())
                            .reservado(false) // si tu Rol tiene 'reservado', mapealo aquí
                            .fechaAlta(rol.getFechaHoraAlta())
                            .fechaBaja(rol.getFechaHoraBaja())
                            .permisos(permisosDTO)
                            .build();
                });
    }

    @Override
    public DTORol obtenerRolCompleto(Long id) throws Exception {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new Exception("Rol no encontrado"));

        // Traigo todo el historial de permisos del rol
        List<RolPermiso> rps = rolPermisoRepository.findByRolIdOrderByFechaHoraAltaAsc(rol.getId());

        // Agrupo por nombre de permiso y construyo Periodos
        Map<String, List<DTORol.Periodo>> porPermiso = new LinkedHashMap<>();
        for (RolPermiso rp : rps) {
            String nom = rp.getPermiso().getNombre();
            porPermiso.computeIfAbsent(nom, k -> new ArrayList<>())
                    .add(DTORol.Periodo.builder()
                            .desde(rp.getFechaHoraAlta())
                            .hasta(rp.getFechaHoraBaja())
                            .build());
        }

        // Armo lista de permisos con períodos
        List<DTORol.PermisoEnRol> permisosDTO = porPermiso.entrySet().stream()
                .map(e -> DTORol.PermisoEnRol.builder()
                        .nombre(e.getKey())
                        .reservado(false) // si tu Permiso tiene 'reservado', mapealo aquí
                        .periodos(e.getValue())
                        .build())
                .toList();

        // Devuelvo DTORol final
        return DTORol.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .reservado(false) // si tu Rol tiene 'reservado', mapealo aquí
                .fechaAlta(rol.getFechaHoraAlta())
                .fechaBaja(rol.getFechaHoraBaja())
                .permisos(permisosDTO)
                .build();
    }


    @Override
    @Transactional
    public void altaRol(DTOAltaRol dto) throws Exception {
        // 1) Validación de nombre duplicado (solo roles activos)
        if (rolRepository.existsByNombreIgnoreCaseAndFechaHoraBajaIsNull(dto.getNombre())) {
        throw new IllegalArgumentException("Ya existe un rol activo con ese nombre");
        }
        LocalDateTime now = LocalDateTime.now();

        Rol rol = Rol.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .fechaHoraAlta(now)
                // .reservado(dto.isReservado()) // si existe el campo en la entidad
                .build();
        rol = rolRepository.save(rol);

        if (dto.getPermisos() != null) {
            for (String nombrePermiso : dto.getPermisos()) {
                Permiso permiso = permisoRepository.findByNombreIgnoreCase(nombrePermiso)
                        .orElseThrow(() -> new Exception("Permiso inexistente: " + nombrePermiso));

                RolPermiso rp = RolPermiso.builder()
                        .rol(rol)
                        .permiso(permiso)
                        .fechaHoraAlta(now)
                        .build();
                rolPermisoRepository.save(rp);
            }
        }
    }


    @Override
    @Transactional
    public void modificarRol(DTOModificarRol dto) throws Exception {
        Rol rol = rolRepository.findById(dto.getId())
                .orElseThrow(() -> new Exception("Rol no encontrado"));

        // Si cambia el nombre, validar que no exista otro activo con ese nombre
        String nombreNuevo = dto.getNombre();
        if (nombreNuevo != null && !nombreNuevo.equalsIgnoreCase(rol.getNombre())) {
            boolean existeOtroActivo = rolRepository.existsByNombreIgnoreCaseAndFechaHoraBajaIsNull(nombreNuevo);
            if (existeOtroActivo) {
                throw new IllegalArgumentException("Ya existe un rol activo con ese nombre");
            }
        }

        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        // rol.setReservado(dto.isReservado()); // si existe el campo
        rolRepository.save(rol);

        LocalDateTime now = LocalDateTime.now();

        // permisos vigentes actuales
        List<RolPermiso> vigentes = rolPermisoRepository.findByRolAndFechaHoraBajaIsNull(rol);
        Set<String> actuales = vigentes.stream()
                .map(rp -> rp.getPermiso().getNombre().toLowerCase())
                .collect(java.util.stream.Collectors.toSet());

        Set<String> nuevos = (dto.getPermisos() == null) ? Set.of()
                : dto.getPermisos().stream().map(String::toLowerCase).collect(java.util.stream.Collectors.toSet());

        // cerrar los que ya no están
        for (RolPermiso rp : vigentes) {
            if (!nuevos.contains(rp.getPermiso().getNombre().toLowerCase())) {
                rp.setFechaHoraBaja(now);
                rolPermisoRepository.save(rp);
            }
        }

        // agregar los nuevos
        for (String pNom : nuevos) {
            if (!actuales.contains(pNom)) {
                Permiso permiso = permisoRepository.findByNombreIgnoreCase(pNom)
                        .orElseThrow(() -> new Exception("Permiso inexistente: " + pNom));
                RolPermiso nuevoRp = RolPermiso.builder()
                        .rol(rol)
                        .permiso(permiso)
                        .fechaHoraAlta(now)
                        .build();
                rolPermisoRepository.save(nuevoRp);
            }
        }
    }


    @Override
    @Transactional
    public void bajaRol(Long id) throws Exception {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new Exception("Rol no encontrado"));

        LocalDateTime now = LocalDateTime.now();

        rol.setFechaHoraBaja(now);
        rolRepository.save(rol);

        List<RolPermiso> vigentes = rolPermisoRepository.findByRolAndFechaHoraBajaIsNull(rol);
        for (RolPermiso rp : vigentes) {
            rp.setFechaHoraBaja(now);
            rolPermisoRepository.save(rp);
        }
    }

    // Alta usuario
    // -------------------------
    @Override
    @Transactional
    public void altaUsuario(DTOAltaUsuario dto) throws Exception {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese username");
        }
        if (usuarioRepository.existsByMail(dto.getMail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese mail");
        }
        if (usuarioRepository.existsByDni(dto.getDni())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese DNI");
        }

        Usuario u = Usuario.builder()
        .nombre(dto.getNombre())
        .apellido(dto.getApellido())
        .username(dto.getUsername())
        .dni(dto.getDni())
        .mail(dto.getMail())
        .fechaNacimiento(dto.getFechaNacimiento() != null ? dto.getFechaNacimiento().atStartOfDay() : null)
        .fechaHoraAlta(LocalDateTime.now())
        .build();

        usuarioRepository.save(u);

        for (Long rolId : dto.getRoles()) {
            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new Exception("Rol no encontrado con id " + rolId));

            if (!rolUsuarioRepository.existsByUsuarioAndRol(u, rol)) {
                rolUsuarioRepository.save(
                        RolUsuario.builder()
                                .usuario(u)
                                .rol(rol)
                                .fechaHoraAlta(LocalDateTime.now())
                                .build()
                );
            }
        }
        try {
        enviarCodigoAltaUsuario(u.getMail());
        log.info("Email de invitación enviado a {}", u.getMail());
        } catch (Exception e) {
        log.error("Fallo al enviar email {}", e.getMessage(), e);
        throw e;
        }

    }

    // -------------------------
    // Modificar usuario
    // -------------------------
    @Override
    @Transactional
    public void modificarUsuario(DTOModificarUsuario dto) throws Exception {
        // 1) Buscar usuario
        Usuario u = usuarioRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // 2) Validaciones de unicidad (si cambia)
        if (!u.getUsername().equals(dto.getUsernameNuevo()) &&
            usuarioRepository.existsByUsername(dto.getUsernameNuevo())) {
            throw new Exception("Username no disponible");
        }
        if (!Objects.equals(u.getMail(), dto.getMail()) &&
            usuarioRepository.existsByMail(dto.getMail())) {
            throw new Exception("Mail ya registrado");
        }
        if (!Objects.equals(u.getDni(), dto.getDni())) {
            // si tenés existsByDni, usarlo; si no, crear uno en el repo de Usuario
            boolean existeDni = usuarioRepository.existsByDni(dto.getDni());
            if (existeDni) throw new Exception("DNI ya registrado");
        }

        // 3) Datos básicos
        u.setUsername(dto.getUsernameNuevo());
        u.setNombre(dto.getNombre());
        u.setApellido(dto.getApellido());
        u.setMail(dto.getMail());
        u.setDni(dto.getDni());
        u.setFechaNacimiento(dto.getFechaNacimiento() != null ? dto.getFechaNacimiento().atStartOfDay() : null);

        usuarioRepository.save(u); // por si querés persistir antes de roles

        // 4) Sincronizar roles
        // 4.1 Actuales activos
        List<RolUsuario> actuales = rolUsuarioRepository.findByUsuarioAndFechaHoraBajaIsNull(u);
        Map<Long, RolUsuario> actualesPorRolId = new HashMap<>();
        for (RolUsuario ru : actuales) {
            actualesPorRolId.put(ru.getRol().getId(), ru);
        }

        // 4.2 Conjunto de roles que deben quedar activos (desde DTO)
        Set<Long> nuevosIds = new HashSet<>(dto.getRoles()); // ids de Rol del DTO

        // 4.3 Dar de baja los que ya no vienen en el DTO
        LocalDateTime ahora = LocalDateTime.now();
        for (RolUsuario ru : actuales) {
            if (!nuevosIds.contains(ru.getRol().getId())) {
                ru.setFechaHoraBaja(ahora);
                rolUsuarioRepository.save(ru);
            }
        }

        // 4.4 Activar / crear los que faltan
        for (Long idRol : nuevosIds) {
            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() -> new Exception("Rol no encontrado: " + idRol));

            // si ya está activo, no toco
            if (actualesPorRolId.containsKey(idRol)) continue;

            // si existe histórico dado de baja y querés "reabrir", podrías buscar el último y limpiar baja.
            // En este diseño, creamos un nuevo registro activo:
            RolUsuario nuevo = RolUsuario.builder()
                    .usuario(u)
                    .rol(rol)
                    .fechaHoraAlta(ahora)
                    .build();
            rolUsuarioRepository.save(nuevo);
        }
    }

    // -------------------------
    // Baja usuario
    // -------------------------
    @Override
    @Transactional
    public void bajaUsuario(String username) throws Exception {
        Usuario u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        if (u.getFechaHoraBaja() != null) {
            throw new IllegalStateException("El usuario ya está dado de baja");
        }

        u.setFechaHoraBaja(LocalDateTime.now());
        usuarioRepository.save(u);

        List<RolUsuario> relaciones = rolUsuarioRepository.findByUsuario(u);
        for (RolUsuario ru : relaciones) {
            ru.setFechaHoraBaja(LocalDateTime.now());
            rolUsuarioRepository.save(ru);
        }
    }

    @Override
    public Page<DTOResultadoBusquedaUsuario> adminBuscarUsuarios(DTOFiltrosBusquedaUsuarios filtros, Pageable pageable) {

        Specification<Usuario> spec = Specification.where(null);

        if (filtros != null) {
            if (filtros.getTexto() != null && !filtros.getTexto().isBlank()) {
                String q = "%" + filtros.getTexto().trim().toLowerCase() + "%";
                spec = spec.and((root, cq, cb) -> cb.or(
                        cb.like(cb.lower(root.get("nombre")), q),
                        cb.like(cb.lower(root.get("apellido")), q),
                        cb.like(cb.lower(root.get("username")), q),
                        cb.like(cb.lower(root.get("mail")), q),
                        cb.like(cb.lower(root.get("dni")), q)
                ));
            }
            if (filtros.getRoles() != null && !filtros.getRoles().isEmpty()) {
                spec = spec.and((root, cq, cb) -> {
                    var joinRU = root.join("rolesUsuario"); // List<RolUsuario>
                    var joinR  = joinRU.join("rol");
                    cq.distinct(true);
                    return joinR.get("id").in(filtros.getRoles());
                });
            }
        }

        Page<Usuario> page = usuarioRepository.findAll(spec, pageable);

        return page.map(u -> DTOResultadoBusquedaUsuario.builder()
                .username(u.getUsername())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .mail(u.getMail())
                .fechaAlta(u.getFechaHoraAlta() == null ? null
                        : u.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .fechaBaja(u.getFechaHoraBaja() == null ? null
                        : u.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build()
        );
    }
    @Override
    public DTOUsuarioCompleto adminObtenerUsuarioCompleto(String username) throws Exception {
        var u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        var rolesVigentes = rolUsuarioRepository.findByUsuario(u).stream()
                .filter(ru -> ru.getFechaHoraBaja() == null)
                .map(ru -> ru.getRol())
                .filter(Objects::nonNull)
                .map(r -> r.getNombre())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());

        return DTOUsuarioCompleto.builder()
                .username(u.getUsername())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .mail(u.getMail())
                .dni(u.getDni())
                .fechaNacimiento(u.getFechaNacimiento() == null ? null
                        : u.getFechaNacimiento().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .alta(u.getFechaHoraAlta() == null ? null
                        : u.getFechaHoraAlta().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .baja(u.getFechaHoraBaja() == null ? null
                        : u.getFechaHoraBaja().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .roles(rolesVigentes)
                .build();
    }

    // Helper en UsuarioServiceImpl (si no lo tenés ya):
    private AbstractMap.SimpleEntry<LocalDateTime, LocalDateTime> rangoSE(SuperEvento se) {
        if (se == null || se.getEventos() == null || se.getEventos().isEmpty()) {
            return new AbstractMap.SimpleEntry<>(null, null);
        }
        LocalDateTime minIni = se.getEventos().stream()
                .map(Evento::getFechaHoraInicio).filter(Objects::nonNull)
                .min(LocalDateTime::compareTo).orElse(null);
        LocalDateTime maxFin = se.getEventos().stream()
                .map(Evento::getFechaHoraFin).filter(Objects::nonNull)
                .max(LocalDateTime::compareTo).orElse(null);
        return new AbstractMap.SimpleEntry<>(minIni, maxFin);
    }

    @Override
    public DTOEventosUsuario adminObtenerEventosUsuario(String username) throws Exception {
        var u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        // --- Organizador ---
        List<DTOEventosUsuario.Organizador> organizador = new ArrayList<>();
        for (AdministradorEvento ae : u.getAdministradoresEvento()) {
            Evento e = ae.getEvento();
            if (e == null) continue;

            String tipo = ae.getTipoAdministradorEvento() != null
                    ? ae.getTipoAdministradorEvento().getNombre()
                    : "";

            // si el tipo es "Organizador"
            if (tipo.equalsIgnoreCase("Organizador")) {
                organizador.add(DTOEventosUsuario.Organizador.builder()
                        .id(e.getId())
                        .nombre(e.getNombre())
                        .fechaDesde(e.getFechaHoraInicio())
                        .fechaHasta(e.getFechaHoraFin())
                        .build());
            }
        }

        // --- Administrador ---
        List<DTOEventosUsuario.Administrador> administrador = new ArrayList<>();
        for (AdministradorEvento ae : u.getAdministradoresEvento()) {
            Evento e = ae.getEvento();
            if (e == null) continue;

            String tipo = ae.getTipoAdministradorEvento() != null
                    ? ae.getTipoAdministradorEvento().getNombre()
                    : "";

            if (tipo.equalsIgnoreCase("Administrador")) {
                List<DTOEventosUsuario.Periodo> periodos = new ArrayList<>();
                periodos.add(new DTOEventosUsuario.Periodo(
                        ae.getFechaHoraAlta(),
                        ae.getFechaHoraBaja()
                ));

                administrador.add(DTOEventosUsuario.Administrador.builder()
                        .id(e.getId())
                        .nombre(e.getNombre())
                        .fechaDesde(e.getFechaHoraInicio())
                        .fechaHasta(e.getFechaHoraFin())
                        .periodos(periodos)
                        .build());
            }
        }

        // --- Participante ---
        List<DTOEventosUsuario.Participante> participante = new ArrayList<>();
        if (u.getInscripciones() != null) {
            for (Inscripcion ins : u.getInscripciones()) {
                Evento e = ins.getEvento();
                if (e != null) {
                    participante.add(DTOEventosUsuario.Participante.builder()
                            .id(e.getId())
                            .nombre(e.getNombre())
                            .fechaDesde(e.getFechaHoraInicio())
                            .fechaHasta(e.getFechaHoraFin())
                            .build());
                }
            }
        }

        return DTOEventosUsuario.builder()
                .organizador(organizador)
                .administrador(administrador)
                .participante(participante)
                .build();
    }


    @Override
    public DTOEspaciosUsuario adminObtenerEspaciosUsuario(String username) throws Exception {
        var u = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new Exception("Usuario no encontrado"));

        List<DTOEspaciosUsuario.Propietario> propietario = new ArrayList<>();
        List<DTOEspaciosUsuario.Administrador> administrador = new ArrayList<>();

        if (u.getAdministradoresEspacio() != null) {
            for (AdministradorEspacio ae : u.getAdministradoresEspacio()) {
                Espacio es = ae.getEspacio();
                if (es == null || ae.getTipoAdministradorEspacio() == null) continue;

                String tipo = ae.getTipoAdministradorEspacio().getNombre();

                if (tipo.equalsIgnoreCase("Propietario")) {
                    propietario.add(DTOEspaciosUsuario.Propietario.builder()
                            .id(es.getId())
                            .nombre(es.getNombre())
                            .fechaDesde(es.getFechaHoraAlta())
                            .fechaHasta(es.getFechaHoraBaja())
                            .build());
                } else if (tipo.equalsIgnoreCase("Administrador")) {
                    List<DTOEspaciosUsuario.Periodo> periodos = new ArrayList<>();
                    periodos.add(new DTOEspaciosUsuario.Periodo(
                            ae.getFechaHoraAlta(),
                            ae.getFechaHoraBaja()
                    ));

                    administrador.add(DTOEspaciosUsuario.Administrador.builder()
                            .id(es.getId())
                            .nombre(es.getNombre())
                            .fechaDesde(es.getFechaHoraAlta())
                            .fechaHasta(es.getFechaHoraBaja())
                            .periodos(periodos)
                            .build());
                }
            }
        }

        return DTOEspaciosUsuario.builder()
                .propietario(propietario)
                .administrador(administrador)
                .build();
    }
    

    @Override
    public DTOSupereventosUsuario adminObtenerSupereventosUsuario(String username) throws Exception {
            Usuario u = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new Exception("Usuario no encontrado"));

            List<DTOSupereventosUsuario.Organizador> organizador = new ArrayList<>();
            List<DTOSupereventosUsuario.Administrador> administrador = new ArrayList<>();

            if (u.getAdministradoresSuperEvento() != null) {
                for (AdministradorSuperEvento ase : u.getAdministradoresSuperEvento()) {
                    SuperEvento se = ase.getSuperEvento();
                    if (se == null || ase.getTipoAdministradorSuperEvento() == null) continue;

                    String tipo = ase.getTipoAdministradorSuperEvento().getNombre();
                    var rango = rangoSE(se);

                    if (tipo.equalsIgnoreCase("Organizador")) {
                        organizador.add(DTOSupereventosUsuario.Organizador.builder()
                                .id(se.getId())
                                .nombre(se.getNombre())
                                .fechaDesde(rango.getKey())
                                .fechaHasta(rango.getValue())
                                .build());
                    } else if (tipo.equalsIgnoreCase("Administrador")) {
                        List<DTOSupereventosUsuario.Periodo> periodos = new ArrayList<>();
                        periodos.add(new DTOSupereventosUsuario.Periodo(
                                ase.getFechaHoraAlta(), ase.getFechaHoraBaja()));

                        administrador.add(DTOSupereventosUsuario.Administrador.builder()
                                .id(se.getId())
                                .nombre(se.getNombre())
                                .fechaDesde(rango.getKey())
                                .fechaHasta(rango.getValue())
                                .periodos(periodos)
                                .build());
                    }
                }
            }

            return DTOSupereventosUsuario.builder()
                    .organizador(organizador)
                    .administrador(administrador)
                    .build();
        }
    // GRupo y interracciones
    // ================== NUEVOS MÉTODOS PARA GRUPOS E INTERACCIONES ==================
    @Override
    public DTOGruposUsuario adminObtenerGruposUsuario(String username) {
        List<UsuarioGrupo> ugs = usuarioGrupoRepository.findAllForUsername(username);

        Map<Long, List<UsuarioGrupo>> porGrupo = ugs.stream()
                .filter(ug -> ug.getGrupo() != null)
                .collect(Collectors.groupingBy(
                        ug -> ug.getGrupo().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<DTOGruposUsuario.GrupoDTO> grupos = new ArrayList<>();

        for (var entry : porGrupo.entrySet()) {
            Grupo g = entry.getValue().get(0).getGrupo();

            List<DTOGruposUsuario.RolDTO> roles = entry.getValue().stream()
                    .map(ug -> new DTOGruposUsuario.RolDTO(
                            ug.getTipoUsuarioGrupo() != null ? ug.getTipoUsuarioGrupo().getNombre() : "Miembro",
                            ug.getFechaHoraAlta(),
                            null // si luego agregan fecha de baja del rol, mapear acá
                    ))
                    .collect(Collectors.toList());

            grupos.add(new DTOGruposUsuario.GrupoDTO(g.getId(), g.getNombre(), roles));
        }

        return new DTOGruposUsuario(grupos);
    }

    @Override
    public DTOInteraccionesUsuario adminObtenerInteraccionesUsuario(String username) {
        List<DTOInteraccionesUsuario.InteraccionDTO> interacciones = new ArrayList<>();

        interacciones = chatRepository.buscar(username, "").stream()
            .filter(c -> c.getMensajes().stream().map(m -> m.getUsuario().getUsername()).toList().contains(username))
            .map(c -> {
                String nombre = "";
                String un = null;
                switch (c.getTipo()) {
                    case DIRECTO -> {
                        if (c.getUsuario1().getUsername().equals(username)) {
                            nombre = "Chat con " + c.getUsuario2().getNombre() + " " + c.getUsuario2().getApellido();
                            un = c.getUsuario2().getUsername();
                        } else {
                            nombre = "Chat con " + c.getUsuario1().getNombre() + " " + c.getUsuario1().getApellido();
                            un = c.getUsuario1().getUsername();
                        }
                    }
                    case EVENTO -> {
                        nombre = "Chat: " + c.getEvento().getNombre();
                    }
                    case SUPEREVENTO -> {
                        nombre = "Chat: " + c.getSuperEvento().getNombre();
                    }
                    case ESPACIO -> {
                        nombre = "Chat: " + c.getEspacio().getNombre();
                    }
                    case GRUPAL -> {
                        nombre = "Chat: " + c.getGrupo().getNombre();
                    }
                }

                Mensaje primero = chatRepository.findFirstMessageByUserInChat(c.getId(), username).orElse(null);
                Mensaje ultimo = chatRepository.findLastMessageByUserInChat(c.getId(), username).orElse(null);

                return DTOInteraccionesUsuario.InteraccionDTO.builder()
                        .id(c.getId())
                        .nombre(nombre)
                        .tipo(c.getTipo().name())
                        .fechaDesde(primero != null ? primero.getFechaHora() : null)
                        .fechaHasta(ultimo != null ? ultimo.getFechaHora() : null)
                        .username(un)
                        .build();
        }).toList();

        return new DTOInteraccionesUsuario(interacciones);
    }

    // ================== HELPERS PRIVADOS (faltaban) ==================
    private static boolean usernameEquals(Usuario u, String username) {
        return u != null && username != null && username.equals(u.getUsername());
    }

    private static String displayName(Usuario u) {
        if (u == null) return "Usuario";
        if ((u.getNombre() != null && !u.getNombre().isBlank()) ||
            (u.getApellido() != null && !u.getApellido().isBlank())) {
            return (Optional.ofNullable(u.getNombre()).orElse("") + " " +
                    Optional.ofNullable(u.getApellido()).orElse("")).trim();
        }
        if (u.getUsername() != null) return u.getUsername();
        if (u.getMail() != null) return u.getMail();
        return "Usuario " + u.getId();
    }

    // --- Denuncias ---
    @Override
    public Page<DTODenunciaUsuario> obtenerDenunciasUsuario(int page) {
        Integer longitudPagina = parametroSistemaService.getInt("longitudPagina", 20);
        Pageable pageable = PageRequest.of(page, longitudPagina);
        return calificacionRepository.obtenerDenuncias(pageable)
            .map(c -> DTODenunciaUsuario.builder()
                .fecha(c.getFechaHora())
                    .descripcion(c.getDescripcion())
                    .denunciante(DTODenunciaUsuario.Persona.builder()
                            .username(c.getAutor().getUsername())
                            .nombre(c.getAutor().getNombre())
                            .apellido(c.getAutor().getApellido())
                            .mail(c.getAutor().getMail())
                        .build())
                    .denunciado(DTODenunciaUsuario.Persona.builder()
                            .username(c.getCalificado().getUsername())
                            .nombre(c.getCalificado().getNombre())
                            .apellido(c.getCalificado().getApellido())
                            .mail(c.getCalificado().getMail())
                            .build())
                .build());
    }


}



