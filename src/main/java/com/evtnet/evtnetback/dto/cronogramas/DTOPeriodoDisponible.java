package com.evtnet.evtnetback.dto.cronogramas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOPeriodoDisponible {
    private Long fechaHoraDesde;
    private Long fechaHoraHasta;
}
