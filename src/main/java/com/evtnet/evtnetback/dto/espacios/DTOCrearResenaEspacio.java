package com.evtnet.evtnetback.dto.espacios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOCrearResenaEspacio {
    private Integer puntuacion;
    private String titulo;
    private String comentario;
    private Long idEspacio;
}
