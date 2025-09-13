package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOModificarRol {
    private Long id;
    private String nombre;
    private String descripcion;
    private boolean reservado;
    private List<String> permisos; // nombres de permisos vigentes tras la modificación
}
