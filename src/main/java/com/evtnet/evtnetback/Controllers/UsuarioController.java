package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Services.UsuarioService;
import com.evtnet.evtnetback.Services.UsuarioServiceImpl;
import com.evtnet.evtnetback.dto.usuarios.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.net.URI;
import org.springframework.http.HttpStatus;

import com.evtnet.evtnetback.dto.comunes.BlobJson;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController extends BaseControllerImpl<Usuario, UsuarioServiceImpl> {

    private final UsuarioService service;

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

    @PostMapping(value = "/registrarseConFoto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DTOAuth> registrarseConFoto(
            @RequestPart("datos") DTORegistrarse datos,
            @RequestPart(value = "foto", required = false) MultipartFile foto
    ) throws Exception {
        byte[] bytes       = (foto != null && !foto.isEmpty()) ? foto.getBytes() : null;
        String nombre      = (foto != null) ? foto.getOriginalFilename() : null;
        String contentType = (foto != null) ? foto.getContentType() : null;

        return ResponseEntity.ok(service.registerConFoto(datos, bytes, nombre, contentType));
    }

    // --- Códigos (registro) ---
    @PutMapping("/enviarCodigo")
    public ResponseEntity<Void> enviarCodigo(@RequestParam(name = "mail", required = false) String mail) throws Exception {
        if (mail != null && !mail.isBlank()) {
            service.enviarCodigo(mail);
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/ingresarCodigo")
    public ResponseEntity<DTOAuth> ingresarCodigo(@RequestParam String codigo) throws Exception {
        return ResponseEntity.ok(service.ingresarCodigo(codigo));
    }

    // --- Disponibilidad ---
    @GetMapping("/verificarUsernameDisponible")
    public ResponseEntity<Map<String, Boolean>> verificarUsernameDisponible(@RequestParam String username) throws Exception {
        boolean disponible = service.usernameDisponible(username);
        return ResponseEntity.ok(Map.of("disponible", disponible));
    }

    // --- Recupero de contraseña ---
    @PutMapping("/enviarCodigoRecuperarContrasena")
    public ResponseEntity<Void> enviarCodigoRecuperarContrasena(@RequestParam String mail) throws Exception {
        service.enviarCodigoRecuperarContrasena(mail);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/recuperarContrasena")
    public ResponseEntity<DTOAuth> recuperarContrasena(@RequestParam String mail,
                                                       @RequestParam String password,
                                                       @RequestParam String codigo) throws Exception {
        return ResponseEntity.ok(service.recuperarContrasena(mail, password, codigo));
    }

    // --- Cambiar contraseña (auth) ---
    @PutMapping("/restablecerContrasena")
    public ResponseEntity<Void> restablecerContrasena(@RequestParam String currentPassword,
                                                      @RequestParam String newPassword) throws Exception {
        service.restablecerContrasena(currentPassword, newPassword);
        return ResponseEntity.noContent().build();
    }

    // --- Perfil ---
    @GetMapping("/obtenerPerfil")
    public ResponseEntity<DTOPerfil> obtenerPerfil(@RequestParam String username) throws Exception {
        return ResponseEntity.ok(service.obtenerPerfil(username));
    }

    @GetMapping("/obtenerFotoDePerfil")
    public ResponseEntity<BlobJson> obtenerFotoDePerfil(@RequestParam String username) throws Exception {
        var foto = service.obtenerFotoDePerfil(username); // ← unificado (ver punto 3)
        if (foto == null || foto.getBytes() == null || foto.getBytes().length == 0) {
            // devolver un PNG mínimo para no romper el front (1x1 transparente)
            byte[] fallback = java.util.Base64.getDecoder().decode(
                "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9UoQXlUAAAAASUVORK5CYII="
            );
            return ResponseEntity.ok(
                new BlobJson(new String(fallback, StandardCharsets.ISO_8859_1), "image/png")
            );
        }
        String binString = new String(foto.getBytes(), StandardCharsets.ISO_8859_1);
        String ct = (foto.getContentType() == null || foto.getContentType().isBlank())
                ? "application/octet-stream" : foto.getContentType();

        return ResponseEntity.ok(new BlobJson(binString, ct));
    }
    
     // (Opcional) Imagen de calificación si después la usás desde el front.
     @GetMapping("/obtenerImagenDeCalificacion")
     public ResponseEntity<byte[]> obtenerImagenDeCalificacion(@RequestParam String username) throws Exception {
         var fr = service.obtenerImagenDeCalificacion(username); // aquí "username" trae "Buena|Media|Mala"
         if (fr == null || fr.getBytes() == null) return ResponseEntity.noContent().build();
         return ResponseEntity.ok()
                 .contentType(org.springframework.http.MediaType.parseMediaType(fr.getContentType()))
                 .body(fr.getBytes());
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
