package com.evtnet.evtnetback.dto.registros;

import java.time.LocalDateTime;

import com.evtnet.evtnetback.config.jackson.LocalDateTimeFlexDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DTOFiltrosRegistro {
    private String[] tipos;
    private String[] subtipos;
    @JsonDeserialize(using = LocalDateTimeFlexDeserializer.class)
    private LocalDateTime fechaHoraDesde;
    @JsonDeserialize(using = LocalDateTimeFlexDeserializer.class)
    private LocalDateTime fechaHoraHasta;
    private String[] usuarios;
}

