package com.evtnet.evtnetback.dto.espacios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOSubespacio {
    private String nombre;
    private String descripcion;
    private int capacidadMaxima;
    private List<Long> disciplinas;
}
