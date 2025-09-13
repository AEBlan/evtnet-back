package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOAltaRol {
    private String nombre;
    private String descripcion;
    private boolean reservado;
    private List<String> permisos; // nombres de permisos
}