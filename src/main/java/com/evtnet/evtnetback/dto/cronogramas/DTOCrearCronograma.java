package com.evtnet.evtnetback.dto.cronogramas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOCrearCronograma {
    private Long idSubEspacio;
    private Long fechaDesde;
    private Long fechaHasta;
    private int diasHaciaAdelante;
}
