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
public class DTOBusquedaEventosPorEspacio {
    private Long idEspacio;
    private String texto;
    private Long fechaDesde;
    private Long fechaHasta;
    private Long horaDesde;
    private Long horaHasta;
    private List<Long> disciplinas;
    private double precioLimite;
}
