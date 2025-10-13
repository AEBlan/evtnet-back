package com.evtnet.evtnetback.dto.cronogramas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOCrearExcepcion {
    private Long idCronograma;
    private Long idExcepcion;
    private Long fechaDesde;
    private Long fechaHasta;
    private Long idTipoExcepcion;
}
