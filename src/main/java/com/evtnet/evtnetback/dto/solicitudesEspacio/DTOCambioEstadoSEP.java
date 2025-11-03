package com.evtnet.evtnetback.dto.solicitudesEspacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOCambioEstadoSEP {
    private Long idSEP;
    private Long idEstado;
    private String descripcion;
}
