package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Entities.Rol;
import com.evtnet.evtnetback.Entities.RolUsuario;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.Repositories.RolRepository;
import com.evtnet.evtnetback.Repositories.UsuarioRepository;
import com.evtnet.evtnetback.Repositories.RolUsuarioRepository;
import com.evtnet.evtnetback.dto.usuarios.*;
import com.evtnet.evtnetback.util.CurrentUser;
import com.evtnet.evtnetback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario, Long> implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RolUsuarioRepository rolUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Códigos (demo en memoria)
    private final Map<String, String> codigosRegistroPorMail = new ConcurrentHashMap<>();
    private final Map<String, String> codigosRecuperoPorMail = new ConcurrentHashMap<>();

    // Directorio para fotos de perfil (montá un volumen)
    @Value("${app.storage.perfiles:/app/uploads/perfiles}")
    private String perfilesDir;

    // Google Sign-In (opcional)
    @Value("${app.google.clientId:}")
    private String googleClientId;

    public UsuarioServiceImpl(
            BaseRepository<Usuario, Long> baseRepository,
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            RolUsuarioRepository rolUsuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        super(baseRepository);
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.rolUsuarioRepository = rolUsuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ---------- AUTH LOCAL ----------
    private DTOAuth authFromUser(Usuario u) {
        List<String> roles = (u.getRolesUsuario() == null) ? List.of()
                : u.getRolesUsuario().stream()
                .map(ru -> ru.getRol().getNombre())
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(u.getUsername(), roles);
        return DTOAuth.builder()
                .token(token)
                .roles(roles) // <- ahora enviamos "roles"
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

        // Crear usuario
        Usuario u = Usuario.builder()
                .username(body.getUsername())
                .mail(body.getMail())
                .nombre(body.getNombre())
                .apellido(body.getApellido())
                .contrasena(passwordEncoder.encode(body.getPassword()))
                .fechaHoraAlta(LocalDateTime.now())
                .build();
        usuarioRepository.save(u);

        // Asignar rol PendienteConfirmación
        Rol rolPend = rolRepository.findByNombre("PendienteConfirmación")
                .orElseThrow(() -> new IllegalStateException("Falta rol PendienteConfirmación"));
        if (!rolUsuarioRepository.existsByUsuarioAndRol(u, rolPend)) {
            rolUsuarioRepository.save(RolUsuario.builder().usuario(u).rol(rolPend).build());
        }

        // Enviar código (en memoria por ahora)
        enviarCodigo(body.getMail());

        return authFromUser(u);
    }

    // ---------- CÓDIGOS (registro/verificación) ----------
    @Override
    public void enviarCodigo(String mail) throws Exception {
        // Validá que el usuario exista (si querés evitar códigos huérfanos)
        usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new Exception("Usuario no encontrado para " + mail));

        String code = generateCode();
        codigosRegistroPorMail.put(mail, code);
        System.out.println("[REGISTRO] Código para " + mail + ": " + code);
        // TODO: integrar emailService para enviarlo realmente
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

        // quitar PendienteConfirmación (si está)
        rolUsuarioRepository.findByUsuarioAndRol(u, rolPend)
                .ifPresent(ru -> rolUsuarioRepository.deleteByUsuarioAndRol(u, rolPend));

        // agregar Usuario (si no está)
        if (!rolUsuarioRepository.existsByUsuarioAndRol(u, rolUsr)) {
            rolUsuarioRepository.save(RolUsuario.builder().usuario(u).rol(rolUsr).build());
        }

        codigosRegistroPorMail.remove(mail);
        return authFromUser(u);
    }

    // ---------- GOOGLE (stub para no romper compilación; reemplazá luego) ----------
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

        // >>> DECLARACIÓN DEL PAYLOAD <<<
        GoogleIdToken.Payload payload = token.getPayload();

        // (Opcional) Validaciones extra
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

        // Busca por mail; si no existe, crea usuario nuevo
        Usuario u = usuarioRepository.findByMail(email).orElseGet(() -> {
            Usuario nu = Usuario.builder()
                    .mail(email)
                    .username(suggestUsername(email))
                    .nombre(name)
                    .contrasena(passwordEncoder.encode(UUID.randomUUID().toString())) // placeholder
                    .fechaHoraAlta(LocalDateTime.now())
                    .build();
            return usuarioRepository.save(nu);
        });

        // Asegura rol "Usuario"
        Rol rolUsr = rolRepository.findByNombre("Usuario")
                .orElseThrow(() -> new IllegalStateException("Falta rol Usuario"));
        if (!rolUsuarioRepository.existsByUsuarioAndRol(u, rolUsr)) {
            rolUsuarioRepository.save(RolUsuario.builder().usuario(u).rol(rolUsr).build());
        }

        // Guarda foto URL de Google si no tienes una local
        if ((u.getFotoPerfil() == null || u.getFotoPerfil().isBlank()) && pictureUrl != null) {
            u.setFotoPerfil(pictureUrl);
            usuarioRepository.save(u);
        }

        return authFromUser(u); // genera tu JWT con roles
    }

    @Override //Para definir contraseña local si no tendra bueno borralo
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
    public void enviarCodigoRecuperarContrasena(String mail) {
        String code = generateCode();
        codigosRecuperoPorMail.put(mail, code);
        System.out.println("[RECUPERO] Código para " + mail + ": " + code);
    }

    @Override
    public DTOAuth recuperarContrasena(String mail, String password, String codigo) throws Exception {
        String esperado = codigosRecuperoPorMail.get(mail);
        if (!Objects.equals(esperado, codigo)) throw new Exception("Código inválido");

        Usuario u = usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        u.setContrasena(passwordEncoder.encode(password));
        usuarioRepository.save(u);
        codigosRecuperoPorMail.remove(mail);

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
        return DTOPerfil.builder()
                .username(u.getUsername())
                .mail(u.getMail())
                .nombreCompleto(((u.getNombre() != null) ? u.getNombre() : "") + " " +
                        ((u.getApellido() != null) ? u.getApellido() : ""))
                .fotoUrl(u.getFotoPerfil())
                .build();
    }

    @Override
    public byte[] obtenerFotoDePerfil(String username) throws Exception {
        Usuario u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
        if (u.getFotoPerfil() == null || u.getFotoPerfil().isBlank()) return null;

        Path p = Paths.get(u.getFotoPerfil());
        if (!Files.exists(p)) return null;
        return Files.readAllBytes(p);
    }

    @Override
    public byte[] obtenerImagenDeCalificacion(String username) {
        return null;
    }

    @Override
    public DTOEditarPerfil obtenerPerfilParaEditar(String username) throws Exception {
        Usuario u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        return DTOEditarPerfil.builder()
                .nombre(u.getNombre())
                .apellido(u.getApellido())
                .dni(u.getDni())
                .cbu(u.getCBU())
                .telefono(null)
                .bio(null)
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

            if (datos.getNewPassword() != null && !datos.getNewPassword().isBlank()) {
                if (datos.getCurrentPassword() == null ||
                        !passwordEncoder.matches(datos.getCurrentPassword(), u.getContrasena()))
                    throw new Exception("Contraseña actual incorrecta");
                u.setContrasena(passwordEncoder.encode(datos.getNewPassword()));
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

    // ---------- Helpers ----------
    private String generateCode() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

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
