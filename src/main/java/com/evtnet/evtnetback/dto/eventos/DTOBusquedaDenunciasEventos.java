package com.evtnet.evtnetback.dto.eventos;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOBusquedaDenunciasEventos {
    private String texto;
    private List<Long> estados;
    private LocalDateTime fechaIngresoDesde;
    private LocalDateTime fechaIngresoHasta;
    private LocalDateTime fechaCambioEstadoDesde;
    private LocalDateTime fechaCambioEstadoHasta;
    private Orden orden;

    public enum Orden {
        FECHA_DENUNCIA_ASC,
        FECHA_DENUNCIA_DESC,
        FECHA_CAMBIO_ESTADO_ASC,
        FECHA_CAMBIO_ESTADO_DESC
    }
}
