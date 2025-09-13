package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOPermisoSimple {
    private String nombre;
    private boolean reservado;
}