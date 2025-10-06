package com.evtnet.evtnetback.dto.modoEvento;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTOModoEvento {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long fechaAlta;
    private Long fechaBaja;
}

