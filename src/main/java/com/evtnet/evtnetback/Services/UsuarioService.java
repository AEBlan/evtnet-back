package com.evtnet.evtnetback.Services;

import com.evtnet.evtnetback.Entities.Usuario;
import com.evtnet.evtnetback.dto.usuarios.*;

public interface UsuarioService extends BaseService<Usuario, Long> {
    DTOAuth login(String mail, String password) throws Exception;
    DTOAuth loginGoogle(String idToken) throws Exception;          // opcional
    DTOAuth register(DTORegistrarse body) throws Exception;

    DTOAuth ingresarCodigo(String codigo) throws Exception;
    void enviarCodigo(String mail) throws Exception;

    boolean usernameDisponible(String username) throws Exception;

    void enviarCodigoRecuperarContrasena(String mail) throws Exception;
    DTOAuth recuperarContrasena(String mail, String password, String codigo) throws Exception;

    void restablecerContrasena(String currentPassword, String newPassword) throws Exception;

    DTOPerfil obtenerPerfil(String username) throws Exception;
    byte[] obtenerFotoDePerfil(String username) throws Exception;
    byte[] obtenerImagenDeCalificacion(String username) throws Exception;

    DTOEditarPerfil obtenerPerfilParaEditar(String username) throws Exception;
    void editarPerfil(DTOEditarPerfil datos, byte[] foto, String nombreArchivo, String contentType) throws Exception;
}
