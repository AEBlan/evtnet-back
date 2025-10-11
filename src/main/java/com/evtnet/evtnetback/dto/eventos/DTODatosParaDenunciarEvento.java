package com.evtnet.evtnetback.dto.eventos;

import lombok.*;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTODatosParaDenunciarEvento {
    private String nombre;
    private boolean inscripto;
    private LocalDateTime fechaDesde;
    private boolean hayDenunciaPrevia;
}
