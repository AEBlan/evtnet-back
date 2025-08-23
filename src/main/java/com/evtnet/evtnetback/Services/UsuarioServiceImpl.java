package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Repositories.BaseRepository;
import com.evtnet.evtnetback.Repositories.UsuarioRepository;
import com.evtnet.evtnetback.dto.usuarios.*;
import com.evtnet.evtnetback.util.CurrentUser;
import com.evtnet.evtnetback.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UsuarioServiceImpl extends BaseServiceImpl<Usuario, Long> implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
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

    public UsuarioServiceImpl(BaseRepository<Usuario, Long> baseRepository,UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,JwtUtil jwtUtil) {
    super(baseRepository);
    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    }

    // ---------- AUTH LOCAL ----------
    private DTOAuth authFromUser(Usuario u) {
        String token = jwtUtil.generateToken(u.getUsername(), List.of("USER"));
        return DTOAuth.builder()
                .token(token)
                .permisos(List.of("USER"))
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
                .contrasena(passwordEncoder.encode(body.getPassword()))
                .fechaHoraAlta(LocalDateTime.now())
                .build();

        usuarioRepository.save(u);
        return authFromUser(u);
    }

    // ---------- GOOGLE SIGN-IN (opcional) ----------
    @Override
    public DTOAuth loginGoogle(String idToken) throws Exception {
        if (googleClientId == null || googleClientId.isBlank())
            throw new Exception("Configura app.google.clientId");

        var verifier = com.google.auth.oauth2.TokenVerifier.newBuilder()
                .setAudience(googleClientId)
                .setIssuer("https://accounts.google.com")
                .build();
        var token = verifier.verify(idToken);

        String email = (String) token.getPayload().get("email");
        String name  = token.getPayload().get("name") != null ? token.getPayload().get("name").toString() : null;

        Usuario u = usuarioRepository.findByMail(email).orElseGet(() -> {
            Usuario nu = Usuario.builder()
                    .mail(email)
                    .username(suggestUsername(email))
                    .nombre(name)
                    .fechaHoraAlta(LocalDateTime.now())
                    .contrasena(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .build();
            return usuarioRepository.save(nu);
        });

        return authFromUser(u);
    }

    // ---------- CÓDIGOS ----------
    @Override
    public DTOAuth ingresarCodigo(String codigo) throws Exception {
        String mail = codigosRegistroPorMail.entrySet().stream()
                .filter(e -> Objects.equals(e.getValue(), codigo))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new Exception("Código inválido"));

        Usuario u = usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        codigosRegistroPorMail.remove(mail);
        return authFromUser(u);
    }

    @Override
    public void enviarCodigo(String mail) {
        String code = generateCode();
        codigosRegistroPorMail.put(mail, code);
        System.out.println("[REGISTRO] Código para " + mail + ": " + code);
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
                .nombreCompleto(((u.getNombre()!=null)?u.getNombre():"") + " " +
                                ((u.getApellido()!=null)?u.getApellido():""))
                .fotoUrl(u.getFotoPerfil())
                .build();
    }

    @Override
    public byte[] obtenerFotoDePerfil(String username) throws Exception {
        Usuario u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));
        if (u.getFotoPerfil()==null || u.getFotoPerfil().isBlank()) return null;

        Path p = Paths.get(u.getFotoPerfil());
        if (!Files.exists(p)) return null;
        return Files.readAllBytes(p);
    }

    @Override
    public byte[] obtenerImagenDeCalificacion(String username) {
        // Implementá cuando definas de dónde sale esta imagen
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
                .telefono(null) // si tu entidad tiene campo teléfono, setearlo
                .bio(null)      // idem bio
                .build();
    }

    @Override
    public void editarPerfil(DTOEditarPerfil datos, byte[] foto, String nombreArchivo, String contentType) throws Exception {
        String usernameActual = CurrentUser.getUsername()
                .orElseThrow(() -> new Exception("No hay usuario autenticado"));
        Usuario u = usuarioRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new Exception("Usuario no encontrado"));

        if (datos != null) {
            if (datos.getNombre()!=null)   u.setNombre(datos.getNombre());
            if (datos.getApellido()!=null) u.setApellido(datos.getApellido());
            if (datos.getDni()!=null)      u.setDni(datos.getDni());
            if (datos.getCbu()!=null)      u.setCBU(datos.getCbu());

            // cambio de contraseña opcional
            if (datos.getNewPassword()!=null && !datos.getNewPassword().isBlank()) {
                if (datos.getCurrentPassword()==null ||
                    !passwordEncoder.matches(datos.getCurrentPassword(), u.getContrasena()))
                    throw new Exception("Contraseña actual incorrecta");
                u.setContrasena(passwordEncoder.encode(datos.getNewPassword()));
            }
        }

        // Foto (opcional)
        if (foto != null && foto.length > 0) {
            Files.createDirectories(Paths.get(perfilesDir));
            String ext = getExtension(nombreArchivo);
            String filename = usernameActual + (ext.isEmpty() ? "" : "." + ext);
            Path destino = Paths.get(perfilesDir).resolve(filename).toAbsolutePath().normalize();
            Files.write(destino, foto, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            u.setFotoPerfil(destino.toString()); // guardo ruta absoluta
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
