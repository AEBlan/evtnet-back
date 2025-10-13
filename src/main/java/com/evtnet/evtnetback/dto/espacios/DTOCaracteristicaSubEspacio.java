package com.evtnet.evtnetback.dto.espacios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOCaracteristicaSubEspacio {
    private Long id;
    private Long idIconoCaracteristica;
    private Long idEspacio;
    private String nombre;
    private String urlIcono;
    private String contentType;
}
