package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.math.BigDecimal;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class DTOPago {
    private String metodo;       // ej: "MP", "EFECTIVO"
    private BigDecimal monto;    // total abonado
    private String referencia;   // id operación / nota
}

