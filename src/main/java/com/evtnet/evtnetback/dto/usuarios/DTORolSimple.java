package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTORolSimple {
    private Long id;
    private String nombre;
    private boolean checked; // si el usuario actual tiene este rol (vigente)
}
