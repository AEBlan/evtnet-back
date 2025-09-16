package com.evtnet.evtnetback.Controllers;

import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.Services.UsuarioService;
import com.evtnet.evtnetback.Services.UsuarioServiceImpl;
import com.evtnet.evtnetback.dto.usuarios.*;
import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.*;


import java.util.Map;

import com.evtnet.evtnetback.dto.comunes.BlobJson;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController extends BaseControllerImpl<Usuario, UsuarioServiceImpl> {

    private final UsuarioService service;
    // --- Auth ---
    @PostMapping("/iniciarSesion")
    public ResponseEntity<DTOAuth> iniciarSesion(@RequestParam String mail, @RequestParam String password) throws Exception {
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
    @PostMapping("/enviarCodigoRecuperarContrasena")
    public ResponseEntity<Void> enviarCodigoRecuperarContrasena(@RequestParam String mail) throws Exception {
        service.enviarCodigoRecuperarContrasena(mail);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/recuperarContrasena")
    public ResponseEntity<DTOAuth> recuperarContrasena(@RequestParam String mail,@RequestParam String password,@RequestParam String codigo) throws Exception {
        return ResponseEntity.ok(service.recuperarContrasena(mail, password, codigo));
    }

    // --- Cambiar contraseña (auth) ---
    @PutMapping("/restablecerContrasena")
    public ResponseEntity<Void> restablecerContrasena(@RequestParam String currentPassword,@RequestParam String newPassword) throws Exception {
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

    // --- Calificaciones ---

    @GetMapping("/obtenerCalificacionTipos")
    public ResponseEntity<java.util.List<DTOCalificacionTipoSimple>> obtenerCalificacionTiposPara(
            @RequestParam String username) throws Exception {
        return ResponseEntity.ok(service.obtenerCalificacionTiposPara(username));
    }

    @GetMapping("/obtenerTiposYMotivosCalificacion")
    public ResponseEntity<java.util.List<DTOTipoCalificacion>> obtenerTiposYMotivosCalificacion() throws Exception {
        return ResponseEntity.ok(service.obtenerTiposYMotivosCalificacion());
    }

    @PostMapping(value = "/calificarUsuario", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> calificarUsuario(@RequestBody DTOCalificacionRequest body) throws Exception {
        service.calificarUsuario(body);
        return ResponseEntity.noContent().build();
    }

    // ====== ENDPOINTS DE ROLES ======

    @GetMapping("/obtenerRoles")
    public ResponseEntity<java.util.List<DTORolSimple>> obtenerRoles() throws Exception {
        return ResponseEntity.ok(service.obtenerRoles());
    }

    @GetMapping("/obtenerPermisos")
    public ResponseEntity<java.util.List<DTOPermisoSimple>> obtenerPermisos() throws Exception {
        return ResponseEntity.ok(service.obtenerPermisos());
    }

    @GetMapping("/obtenerRolCompleto")
    public ResponseEntity<DTORol> obtenerRolCompleto(@RequestParam Long id) throws Exception {
        return ResponseEntity.ok(service.obtenerRolCompleto(id));
    }

    @GetMapping("/obtenerRolesCompletos")
    public ResponseEntity<Page<DTORol>> obtenerRolesCompletos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) throws Exception {
        Pageable pageable = PageRequest.of(page, size, Sort.by("nombre").ascending());
        return ResponseEntity.ok(service.obtenerRolesCompletos(pageable));
    }

    @PostMapping("/altaRol")
    public ResponseEntity<Void> altaRol(@RequestBody DTOAltaRol dto) throws Exception {
        service.altaRol(dto);
        return ResponseEntity.noContent().build();
    }

    
    @PutMapping("/modificarRol")
    public ResponseEntity<Void> modificarRol(@RequestBody DTOModificarRol dto) throws Exception {
        service.modificarRol(dto);
        return ResponseEntity.noContent().build();
    }

    
    @DeleteMapping("/bajaRol")
    public ResponseEntity<Void> bajaRol(@RequestParam Long id) throws Exception {
        service.bajaRol(id);
        return ResponseEntity.noContent().build();
    }

    // === USUARIO (gestión directa por admin) ===

    @DeleteMapping("/bajaUsuario")
    public ResponseEntity<Void> bajaUsuario(@RequestParam String username) throws Exception {
        service.bajaUsuario(username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/altaUsuario")
    public ResponseEntity<Void> altaUsuario(@RequestBody DTOAltaUsuario dto) throws Exception {
        service.altaUsuario(dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/modificarUsuario")
    public ResponseEntity<Void> modificarUsuario(@RequestBody DTOModificarUsuario dto) throws Exception {
        service.modificarUsuario(dto);
        return ResponseEntity.noContent().build();
    }

    // --- Admin: búsqueda de usuarios (paginado) ---
    @PutMapping("/adminBuscarUsuarios")
    public ResponseEntity<org.springframework.data.domain.Page<DTOResultadoBusquedaUsuario>> adminBuscarUsuarios(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestBody DTOFiltrosBusquedaUsuarios filtros
    ) throws Exception {
        var pageable = org.springframework.data.domain.PageRequest.of(
                page, 10, org.springframework.data.domain.Sort.by("fechaHoraAlta").descending()
        );
        return ResponseEntity.ok(service.adminBuscarUsuarios(filtros, pageable));
    }

    // --- Admin: usuario completo ---
    @GetMapping("/adminObtenerUsuarioCompleto")
    public ResponseEntity<DTOUsuarioCompleto> adminObtenerUsuarioCompleto(
            @RequestParam String username
    ) throws Exception {
        return ResponseEntity.ok(service.adminObtenerUsuarioCompleto(username));
    }
    // --- Admin: info adicional del usuario (eventos, espacios, supereventos) ---
    @GetMapping("/adminObtenerEventosUsuario")
    public ResponseEntity<DTOEventosUsuario> adminObtenerEventosUsuario(@RequestParam String username) throws Exception {
        return ResponseEntity.ok(service.adminObtenerEventosUsuario(username));
    }

    @GetMapping("/adminObtenerEspaciosUsuario")
    public ResponseEntity<DTOEspaciosUsuario> adminObtenerEspaciosUsuario(@RequestParam String username) throws Exception {
        return ResponseEntity.ok(service.adminObtenerEspaciosUsuario(username));
    }

    @GetMapping("/adminObtenerSupereventosUsuario")
    public ResponseEntity<DTOSupereventosUsuario> adminObtenerSupereventosUsuario(@RequestParam String username) throws Exception {
        return ResponseEntity.ok(service.adminObtenerSupereventosUsuario(username));
    }
    // --- Admin: info adicional del usuario (grupos, interacciones) ---

    @GetMapping("/adminObtenerGruposUsuario")
    public ResponseEntity<DTOGruposUsuario> adminObtenerGruposUsuario(@RequestParam String username) {
        return ResponseEntity.ok(service.adminObtenerGruposUsuario(username));
    }

    @GetMapping("/adminObtenerInteraccionesUsuario")
    public ResponseEntity<DTOInteraccionesUsuario> adminObtenerInteraccionesUsuario(@RequestParam String username) {
        return ResponseEntity.ok(service.adminObtenerInteraccionesUsuario(username));
    }
    // --- Denuncias ---
    @GetMapping("/obtenerDenunciasUsuario")
    public ResponseEntity<Page<DTODenunciaUsuario>> obtenerDenunciasUsuario(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        String username = currentUsername();
        return ResponseEntity.ok(service.obtenerDenunciasUsuario(username, page, size));
    }

    private String currentUsername() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.getName() != null && !auth.getName().isBlank())
                ? auth.getName()
                : "luly"; // fallback para dev
    }
}
