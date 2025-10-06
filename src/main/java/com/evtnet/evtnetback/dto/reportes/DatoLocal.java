package com.evtnet.evtnetback.dto.reportes;

import lombok.*;
import java.time.LocalDateTime;

@Data @AllArgsConstructor
public class DatoLocal {
    private String evento;
    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;
    private long participantes;
}
