package com.evtnet.evtnetback.dto.eventos;

import com.evtnet.evtnetback.config.jackson.LocalDateTimeFlexDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
    @JsonDeserialize(using = LocalDateTimeFlexDeserializer.class)
    private LocalDateTime fechaIngresoDesde;
    @JsonDeserialize(using = LocalDateTimeFlexDeserializer.class)
    private LocalDateTime fechaIngresoHasta;
    @JsonDeserialize(using = LocalDateTimeFlexDeserializer.class)
    private LocalDateTime fechaCambioEstadoDesde;
    @JsonDeserialize(using = LocalDateTimeFlexDeserializer.class)
    private LocalDateTime fechaCambioEstadoHasta;
    private Orden orden;

    public enum Orden {
        FECHA_DENUNCIA_ASC,
        FECHA_DENUNCIA_DESC,
        FECHA_CAMBIO_ESTADO_ASC,
        FECHA_CAMBIO_ESTADO_DESC
    }
}
