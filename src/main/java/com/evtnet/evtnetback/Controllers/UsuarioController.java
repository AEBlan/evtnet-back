package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Services.UsuarioServiceImpl;
import com.evtnet.evtnetback.dto.usuarios.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController extends BaseControllerImpl<Usuario, UsuarioServiceImpl> {

    private final UsuarioServiceImpl service;
    public UsuarioController(UsuarioServiceImpl service) { this.service = service; }

    // --- Auth ---
    @PostMapping("/iniciarSesion")
    public ResponseEntity<DTOAuth> iniciarSesion(@RequestParam String mail,
                                                 @RequestParam String password) throws Exception {
        return ResponseEntity.ok(service.login(mail, password));
    }

    @PostMapping("/loginGoogle")
    public ResponseEntity<DTOAuth> loginGoogle(@RequestParam String idToken) throws Exception {
        return ResponseEntity.ok(service.loginGoogle(idToken));
    }

    @PostMapping("/registrarse")
    public ResponseEntity<DTOAuth> registrarse(@RequestBody DTORegistrarse dto) throws Exception {
        return ResponseEntity.ok(service.register(dto));
    }

    // --- Códigos (registro / verificación de email) ---
    @PutMapping("/enviarCodigo") // PUBLICO
    public ResponseEntity<Void> enviarCodigo(@RequestParam String mail) {
        service.enviarCodigo(mail);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> enviarCodigoRecuperarContrasena(@RequestParam String mail) {
        service.enviarCodigoRecuperarContrasena(mail);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/recuperarContrasena") // PUBLICO según tu tabla
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
        return ResponseEntity.ok().build();
    }

    // --- Perfil (AUTENTICADO según tu tabla) ---
    @GetMapping("/obtenerPerfil") // requiere token
    public ResponseEntity<DTOPerfil> obtenerPerfil(@RequestParam String username) throws Exception {
        return ResponseEntity.ok(service.obtenerPerfil(username));
    }

    @GetMapping(value = "/obtenerFotoDePerfil") // tu tabla dice "sí" auth
    public ResponseEntity<byte[]> obtenerFotoDePerfil(@RequestParam String username) throws Exception {
        byte[] img = service.obtenerFotoDePerfil(username);
        if (img == null) return ResponseEntity.noContent().build();
        // Tip: si la guardás como jpg/png, devolvé content-type dinámico si querés.
        return ResponseEntity.ok(img);
    }

    @GetMapping(value = "/obtenerImagenDeCalificacion") // tu tabla dice "no" auth
    public ResponseEntity<byte[]> obtenerImagenDeCalificacion(@RequestParam String username) throws Exception {
        byte[] img = service.obtenerImagenDeCalificacion(username);
        if (img == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(img);
    }

    @GetMapping("/obtenerPerfilParaEditar") // requiere token
    public ResponseEntity<DTOEditarPerfil> obtenerPerfilParaEditar(@RequestParam String username) throws Exception {
        return ResponseEntity.ok(service.obtenerPerfilParaEditar(username));
    }

    @PutMapping(value = "/editarPerfil", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // requiere token
    public ResponseEntity<Void> editarPerfil(@RequestPart("datos") DTOEditarPerfil datos,
                                             @RequestPart(value = "foto", required = false) MultipartFile foto) throws Exception {
        byte[] bytes = (foto != null && !foto.isEmpty()) ? foto.getBytes() : null;
        String nombre = (foto != null) ? foto.getOriginalFilename() : null;
        String ct     = (foto != null) ? foto.getContentType() : null;
        service.editarPerfil(datos, bytes, nombre, ct);
        return ResponseEntity.ok().build();
    }
}