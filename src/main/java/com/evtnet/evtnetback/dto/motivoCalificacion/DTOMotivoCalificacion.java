package com.evtnet.evtnetback.dto.motivoCalificacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DTOMotivoCalificacion {
    private Long id;
    private String nombre;
    private Long idTipoCalificacion;
    private String nombreTipoCalificacion;
}
