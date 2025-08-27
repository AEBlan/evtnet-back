package com.evtnet.evtnetback.dto.usuario;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class DTOPerfilParaEditar {
    private String username;
    private String nombre;
    private String apellido;
    private String dni;
    private String cbu;
    private String mail;
    private String fotoPerfil;
}