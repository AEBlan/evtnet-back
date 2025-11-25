package com.evtnet.evtnetback.dto.comprobante;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOComprobanteSimple {
    private Long numero;
    private String concepto;
    private Long fechaHoraEmision; // epoch millis
    private Double monto;
}
