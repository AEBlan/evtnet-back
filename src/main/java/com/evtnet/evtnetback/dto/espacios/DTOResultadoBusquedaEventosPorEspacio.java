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
public class DTOResultadoBusquedaEventosPorEspacio {
    private Long id;
    private String nombre;
    private Long fechaHoraInicio;
    private double precio;
    private List<String> disciplinas;
}
