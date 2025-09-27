package com.evtnet.evtnetback.dto.estadoSEP;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTOEstadoSEP {
    private Long id;
    private String nombre;
    private String descripcion;
}
