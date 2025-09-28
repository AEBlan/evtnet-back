package com.evtnet.evtnetback.dto.comisionPorOrganizacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTOComisionPorOrganizacion {
    private Long id;
    private BigDecimal montoLimite;
    private BigDecimal porcentaje;
    private Long fechaDesde;
    private Long fechaHasta;
}
