package com.evtnet.evtnetback.dto.tipoCalificacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DTOTipoCalificacionSelect {
    private Long id;
    private String nombre;
}
