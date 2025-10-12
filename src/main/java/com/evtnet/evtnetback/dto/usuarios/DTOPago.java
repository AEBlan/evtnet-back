package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.math.BigDecimal;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class DTOPago {
    private BigDecimal paymentId;
    private String status;
    private String external_reference;
    private String preference_id;
}

