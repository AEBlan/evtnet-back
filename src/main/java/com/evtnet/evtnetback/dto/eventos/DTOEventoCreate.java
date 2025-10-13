// src/main/java/com/evtnet/evtnetback/dto/eventos/DTOEventoCreate.java
package com.evtnet.evtnetback.dto.eventos;

import com.evtnet.evtnetback.dto.disciplinaevento.DTODisciplinaEventoCreate;
import com.evtnet.evtnetback.dto.disciplinas.DTODisciplinaRef;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DTOEventoCreate {

    private String nombre;
    private String descripcion;

    @JsonDeserialize(using = IsoOrEpochLocalDateTimeDeserializer.class)
    private LocalDateTime fechaHoraInicio;

    @JsonDeserialize(using = IsoOrEpochLocalDateTimeDeserializer.class)
    private LocalDateTime fechaHoraFin;

    private BigDecimal precioInscripcion;
    private Integer cantidadMaximaInvitados;
    private Integer cantidadMaximaParticipantes;
    private BigDecimal precioOrganizacion;

    private Long superEventoId;

    @JsonAlias({"idSubespacio", "subEspacioId", "idEspacio"}) // acepta los tres
    private Long subEspacioId;

    private List<DTODisciplinaEventoCreate> disciplinasEvento;
}
