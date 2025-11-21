package com.evtnet.evtnetback.service;

import com.evtnet.evtnetback.entity.Usuario;
import com.evtnet.evtnetback.dto.usuarios.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

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
    FotoResponseString obtenerImagenDeCalificacion(String nombre) throws Exception;

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

        public class FotoResponseString {
        private final String content;
        private final String contentType;

        public FotoResponseString(String content, String contentType) {
            this.content = content;
            this.contentType = (contentType == null || contentType.isBlank())
                    ? "application/octet-stream" : contentType;
        }

        public String getContent() { return content; }
        public String getContentType() { return contentType; }
    }
    // Perfil editable
    DTOEditarPerfil obtenerPerfilParaEditar(String username) throws Exception;
    void editarPerfil(DTOEditarPerfil datos, byte[] foto, String nombreArchivo, String contentType) throws Exception;

    List<DTOCalificacionTipoSimple> obtenerCalificacionTiposPara(String username) throws Exception;
    List<DTOTipoCalificacion> obtenerTiposYMotivosCalificacion() throws Exception;
    void calificarUsuario(DTOCalificacionRequest body) throws Exception;

    // === Roles ===
    List<DTORolSimple> obtenerRoles() throws Exception;
    Page<DTORol> obtenerRolesCompletos(Pageable pageable) throws Exception;
    DTORol obtenerRolCompleto(Long id) throws Exception;
    List<DTOPermisoSimple> obtenerPermisos() throws Exception;
    void altaRol(DTOAltaRol dto) throws Exception;
    void modificarRol(DTOModificarRol dto) throws Exception;
    void bajaRol(Long id) throws Exception;

    // === Usuarios (admin) ===
    void bajaUsuario(String username) throws Exception;
    void altaUsuario(DTOAltaUsuario dto) throws Exception;
    void modificarUsuario(DTOModificarUsuario dto) throws Exception;

    // Buscar usuarios (admin)
    Page<DTOResultadoBusquedaUsuario> adminBuscarUsuarios(DTOFiltrosBusquedaUsuarios filtros, Pageable pageable) throws Exception;
    DTOUsuarioCompleto adminObtenerUsuarioCompleto(String username) throws Exception;

    // Obtener eventos/espacios/supereventos de un usuario (admin)
    DTOEventosUsuario adminObtenerEventosUsuario(String username) throws Exception;
    DTOEspaciosUsuario adminObtenerEspaciosUsuario(String username) throws Exception;
    DTOSupereventosUsuario adminObtenerSupereventosUsuario(String username) throws Exception;

    // Obtener grupos/interacciones de un usuario (admin)
    DTOGruposUsuario adminObtenerGruposUsuario(String username);
    DTOInteraccionesUsuario adminObtenerInteraccionesUsuario(String username);

    //Denuncias
    Page<DTODenunciaUsuario> obtenerDenunciasUsuario(int page);


}
