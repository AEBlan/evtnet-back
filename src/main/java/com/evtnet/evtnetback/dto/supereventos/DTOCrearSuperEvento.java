package com.evtnet.evtnetback.dto.supereventos;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOCrearSuperEvento {
    private String nombre;
    private String descripcion;
}
