package com.evtnet.evtnetback.dto.supereventos;

import java.time.LocalDateTime;

import com.evtnet.evtnetback.config.jackson.LocalDateTimeFlexDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOResultadoBusquedaMisSuperEventos {
    private Long id;
    private String nombre;
    private Long fechaDesde;
    private Long fechaHasta;
    private Integer eventosFuturos;
    private Integer eventosTotales;
}
