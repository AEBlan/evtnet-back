package com.evtnet.evtnetback.dto.disciplinas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOBusquedaDisciplina {
    private String texto;
    private Long fechaDesde;
    private Long fechaHasta;
}
