package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOPreferenciaPago {
    private String concepto;
    private BigDecimal montoBruto;
    private BigDecimal comision;
    private String preference_id;
    private String public_key;
    private boolean completada;
}

