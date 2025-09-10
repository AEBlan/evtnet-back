package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.dto.usuarios.*;

public interface UsuarioService extends BaseService<Usuario, Long> {

    // === Auth / Registro ===
    DTOAuth login(String mail, String password) throws Exception;
    DTOAuth loginGoogle(String idToken) throws Exception;
    DTOAuth register(DTORegistrarse body) throws Exception;
    DTOAuth registerConFoto(DTORegistrarse body, byte[] foto, String nombreArchivo, String contentType) throws Exception;

    // === Códigos (registro/verificación) ===
    DTOAuth ingresarCodigo(String codigo) throws Exception;
    void enviarCodigo(String mail) throws Exception;

    // === Disponibilidad ===
    boolean usernameDisponible(String username) throws Exception;

    // === Recupero de contraseña ===
    void enviarCodigoRecuperarContrasena(String mail) throws Exception;
    DTOAuth recuperarContrasena(String mail, String password, String codigo) throws Exception;

    // === Cambio de contraseña (autenticado) ===
    void restablecerContrasena(String currentPassword, String newPassword) throws Exception;
    void definirContrasena(String mail, String nuevaPassword) throws Exception;

    // === Perfil ===
    DTOPerfil obtenerPerfil(String username) throws Exception;

    // Imagenes
    FotoResponse obtenerFotoDePerfil(String username) throws Exception;              // ← unificado
    FotoResponse obtenerImagenDeCalificacion(String username) throws Exception;      // ← unificado

    class FotoResponse {
        private final byte[] bytes;
        private final String contentType;
        public FotoResponse(byte[] bytes, String contentType) {
            this.bytes = bytes;
            this.contentType = (contentType == null || contentType.isBlank())
                    ? "application/octet-stream" : contentType;
        }
        public byte[] getBytes() { return bytes; }
        public String getContentType() { return contentType; }
    }
    // Perfil editable
    DTOEditarPerfil obtenerPerfilParaEditar(String username) throws Exception;
    void editarPerfil(DTOEditarPerfil datos, byte[] foto, String nombreArchivo, String contentType) throws Exception;
}
