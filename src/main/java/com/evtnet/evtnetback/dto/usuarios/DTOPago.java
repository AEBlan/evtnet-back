package com.evtnet.evtnetback.dto.usuarios;

import com.evtnet.evtnetback.entity.Usuario;
import lombok.*;
import java.math.BigDecimal;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class DTOPago {
    private String paymentId;
    private String status;
    private String external_reference;
    private String preference_id;
    private Usuario destinatario;
}

