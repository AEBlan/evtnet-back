package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Rol;
import com.evtnet.evtnetback.Entities.RolUsuario;
import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.Repositories.CalificacionRepository;
import com.evtnet.evtnetback.Repositories.RolRepository;
import com.evtnet.evtnetback.Repositories.RolUsuarioRepository;
import com.evtnet.evtnetback.Repositories.UsuarioRepository;
import com.evtnet.evtnetback.dto.usuarios.*;
import com.evtnet.evtnetback.security.JwtUtil;
import com.evtnet.evtnetback.util.CurrentUser;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;                         // <<< IMPORT NECESARIO
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario, Long> implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MailService mailService; // inyectado para enviar el mail
    private final CalificacionRepository calificacionRepository;

    // Directorio para fotos de perfil (montá un volumen)
    @Value("${app.storage.perfiles:/app/uploads/perfiles}")
    private String perfilesDir;

    // Google Sign-In (opcional)
    @Value("${app.google.clientId:}")
    private String googleClientId;

    @Value("${app.storage.calificaciones:/app/uploads/calificaciones}")
    private String calificacionesDir;

    public UsuarioServiceImpl(
            BaseRepository<Usuario, Long> baseRepository,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            RolUsuarioRepository rolUsuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            MailService mailService,
            CalificacionRepository calificacionRepository
    ) {
        super(baseRepository);
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.mailService = mailService;
        this.calificacionRepository = calificacionRepository;
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

    // ---------- AUTH LOCAL ----------
    private DTOAuth authFromUser(Usuario u) {
        List<String> permisos = rolUsuarioRepository.findPermisosByUsername(u.getUsername());
        if (permisos == null) permisos = List.of(); // por las dudas
    
        String token = jwtUtil.generateToken(u.getUsername(), permisos);
        return DTOAuth.builder()
                .token(token)
                .permisos(permisos)
                .username(u.getUsername())
                .build();
    }

    @Override
    public DTOAuth login(String mail, String password) throws Exception {
        Usuario u = usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
        if (!passwordEncoder.matches(password, u.getContrasena()))
            throw new Exception("Credenciales inválidas");
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
                .build();
        usuarioRepository.save(u);

        Rol rolPend = rolRepository.findByNombre("PendienteConfirmación")
                .orElseThrow(() -> new IllegalStateException("Falta rol PendienteConfirmación"));
        if (!rolUsuarioRepository.existsByUsuarioAndRol(u, rolPend)) {
            rolUsuarioRepository.save(
                    RolUsuario.builder().usuario(u).rol(rolPend).fechaHoraAlta(LocalDateTime.now()).build()
            );
        }
        
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
        String subject = "Código de verificación de cuenta";
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
        String[] tipos = { "Buena", "Media", "Mala" };

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

        return DTOPerfil.builder()
                .username(u.getUsername())
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .mail(u.getMail())
                .dni(u.getDni())
                .fechaNacimiento(fnac)
                .calificaciones(items)          // <- ahora sí
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

        if (foto != null && foto.length > 0) {
            Files.createDirectories(Paths.get(perfilesDir));
            String ext = getExtension(nombreArchivo);
            String filename = usernameActual + (ext.isEmpty() ? "" : "." + ext);
            Path destino = Paths.get(perfilesDir).resolve(filename).toAbsolutePath().normalize();
            Files.write(destino, foto, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            u.setFotoPerfil(destino.toString());
        }

        usuarioRepository.save(u);
    }

    @Override
    public FotoResponse obtenerFotoDePerfil(String username) throws Exception {
        var u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
        Path path = (u.getFotoPerfil() == null || u.getFotoPerfil().isBlank()) ? null : Paths.get(u.getFotoPerfil());
        if (path == null || !Files.exists(path)) {
            // devolvé null y que el controller aplique fallback, o devolvé el fallback aquí
            return null;
        }
        String ct = Files.probeContentType(path);
        return new FotoResponse(Files.readAllBytes(path), ct);
    }


    @Override
    public FotoResponse obtenerImagenDeCalificacion(String username) throws Exception {
        // En el front este parámetro es el "nombre de la calificación" (Buena/Media/Mala)
        if (username == null || username.isBlank()) return null;

        // normalizo y valido
        String tipo = username.trim().toLowerCase(Locale.ROOT);
        switch (tipo) {
            case "buena":
            case "media":
            case "mala":
                break;
            default:
                // si te interesa, podrías mapear sinónimos aquí (p.ej. "baja" -> "mala")
                return null;
        }

        // carpeta base (agregá arriba en la clase: @Value("${app.storage.calificaciones:/app/uploads/calificaciones}") private String calificacionesDir;)
        Path base = Paths.get(calificacionesDir);
        Files.createDirectories(base);

        // solo PNG
        Path img = base.resolve(tipo + ".png").toAbsolutePath().normalize();
        if (Files.exists(img)) {
            return new FotoResponse(Files.readAllBytes(img), "image/png");
        }

        // fallback si no existe el específico
        Path def = base.resolve("_default.png").toAbsolutePath().normalize();
        if (Files.exists(def)) {
            return new FotoResponse(Files.readAllBytes(def), "image/png");
        }

        // si no hay nada, que el front use su placeholder
        return null;
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
}
