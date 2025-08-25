package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Services.UsuarioService; // ← interfaz
import com.evtnet.evtnetback.Services.UsuarioServiceImpl; // si tu BaseControllerImpl lo requiere en el genérico
import com.evtnet.evtnetback.dto.usuarios.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController extends BaseControllerImpl<Usuario, UsuarioServiceImpl> {

    private final UsuarioService service;  // ← interfaz

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // --- Auth ---
    @PostMapping("/iniciarSesion")
    public ResponseEntity<DTOAuth> iniciarSesion(@RequestParam String mail,
                                                 @RequestParam String password) throws Exception {
        return ResponseEntity.ok(service.login(mail, password));
    }

    @PostMapping("/loginGoogle")
    public ResponseEntity<DTOAuth> loginGoogle(@RequestBody(required = false) DTOLoginGoogle body, @RequestParam(value = "idToken", required = false) String idToken
    ) throws Exception {
        String token = (body != null && body.getIdToken() != null && !body.getIdToken().isBlank())
                ? body.getIdToken()
                : idToken;
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("idToken requerido");
        }
        return ResponseEntity.ok(service.loginGoogle(token));
    }

    @PutMapping("/definirContrasena")
    public ResponseEntity<Void> definirContrasena(@RequestBody DTOSetPassword dto) throws Exception {
        service.definirContrasena(dto.getMail(), dto.getNuevaPassword());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/registrarse")
    public ResponseEntity<DTOAuth> registrarse(@RequestBody DTORegistrarse dto) throws Exception {
        return ResponseEntity.ok(service.register(dto));
    }

    // --- Códigos (registro / verificación de email) ---
    @PutMapping("/enviarCodigo") // PUBLICO
    public ResponseEntity<Void> enviarCodigo(@RequestParam String mail) throws Exception {
        service.enviarCodigo(mail);                // ← usar 'mail', no 'email'
        return ResponseEntity.noContent().build(); // ← evita HttpStatus import
    }

    @PostMapping("/ingresarCodigo") // PUBLICO
    public ResponseEntity<DTOAuth> ingresarCodigo(@RequestParam String codigo) throws Exception {
        return ResponseEntity.ok(service.ingresarCodigo(codigo));
    }

    @GetMapping("/verificarUsernameDisponible") // PUBLICO
    public ResponseEntity<Map<String, Boolean>> verificarUsernameDisponible(@RequestParam String username) throws Exception {
        boolean disponible = service.usernameDisponible(username);
        return ResponseEntity.ok(Map.of("disponible", disponible));
    }

    // --- Recupero de contraseña ---
    @PutMapping("/enviarCodigoRecuperarContrasena") // PUBLICO
    public ResponseEntity<Void> enviarCodigoRecuperarContrasena(@RequestParam String mail) throws Exception {
        service.enviarCodigoRecuperarContrasena(mail);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/recuperarContrasena") // PUBLICO
    public ResponseEntity<DTOAuth> recuperarContrasena(@RequestParam String mail,
                                                       @RequestParam String password,
                                                       @RequestParam String codigo) throws Exception {
        return ResponseEntity.ok(service.recuperarContrasena(mail, password, codigo));
    }

    // --- Cambiar contraseña (AUTENTICADO) ---
    @PutMapping("/restablecerContrasena") // requiere token
    public ResponseEntity<Void> restablecerContrasena(@RequestParam String currentPassword,
                                                      @RequestParam String newPassword) throws Exception {
        service.restablecerContrasena(currentPassword, newPassword);
        return ResponseEntity.noContent().build();
    }

    // --- Perfil (AUTENTICADO según tu tabla) ---
    @GetMapping("/obtenerPerfil") // requiere token
    public ResponseEntity<DTOPerfil> obtenerPerfil(@RequestParam String username) throws Exception {
        return ResponseEntity.ok(service.obtenerPerfil(username));
    }

    @GetMapping(value = "/obtenerFotoDePerfil")
    public ResponseEntity<byte[]> obtenerFotoDePerfil(@RequestParam String username) throws Exception {
        byte[] img = service.obtenerFotoDePerfil(username);
        if (img == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(img);
    }

    @GetMapping(value = "/obtenerImagenDeCalificacion")
    public ResponseEntity<byte[]> obtenerImagenDeCalificacion(@RequestParam String username) throws Exception {
        byte[] img = service.obtenerImagenDeCalificacion(username);
        if (img == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(img);
    }

    @GetMapping("/obtenerPerfilParaEditar")
    public ResponseEntity<DTOEditarPerfil> obtenerPerfilParaEditar(@RequestParam String username) throws Exception {
        return ResponseEntity.ok(service.obtenerPerfilParaEditar(username));
    }

    @PutMapping(value = "/editarPerfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> editarPerfil(@RequestPart("datos") DTOEditarPerfil datos,
                                             @RequestPart(value = "foto", required = false) MultipartFile foto) throws Exception {
        byte[] bytes = (foto != null && !foto.isEmpty()) ? foto.getBytes() : null;
        String nombre = (foto != null) ? foto.getOriginalFilename() : null;
        String ct     = (foto != null) ? foto.getContentType() : null;
        service.editarPerfil(datos, bytes, nombre, ct);
        return ResponseEntity.noContent().build();
    }
}
