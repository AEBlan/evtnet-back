package com.evtnet.evtnetback.dto.supereventos;

import java.time.LocalDateTime;

import com.evtnet.evtnetback.config.jackson.LocalDateTimeFlexDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOBusquedaMisSuperEventos {
    private String texto;

    @JsonDeserialize(using = LocalDateTimeFlexDeserializer.class)
    private LocalDateTime fechaDesde;
    @JsonDeserialize(using = LocalDateTimeFlexDeserializer.class)
    private LocalDateTime fechaHasta;
}
