package com.evtnet.evtnetback.dto.eventos;

import com.evtnet.evtnetback.dto.disciplinaevento.DTODisciplinaEventoCreate;
import com.evtnet.evtnetback.dto.usuarios.DTOPago;
import com.evtnet.evtnetback.json.IsoOrEpochLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO usado para la creación de un evento.
 * Alineado con la lógica de horarios, subespacios y validaciones de permisos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DTOEventoCreate {

    private String nombre;
    private String descripcion;

    @JsonAlias({"idSubespacio", "subEspacioId", "idEspacio"})
    private Long subEspacioId;
    private boolean usarCronograma;

    @JsonDeserialize(using = IsoOrEpochLocalDateTimeDeserializer.class)
    private LocalDateTime fechaHoraInicio;

    @JsonDeserialize(using = IsoOrEpochLocalDateTimeDeserializer.class)
    private LocalDateTime fechaHoraFin;

    private Long horarioId;

    private List<Long> disciplinas;

    private Double precio;

    private int maxParticipantes;

    private DTOPago pago;
}
