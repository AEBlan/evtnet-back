package com.evtnet.evtnetback.dto.tipoCalificacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DTOTipoCalificacion {
    private Long id;
    private String nombre;
    private String url;
    private Long fechaBaja;
    private String contentType;
}
