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
@JsonIgnoreProperties(ignoreUnknown = true) // ignora claves extra del front
public class DTOEventoCreate {

    private String nombre;
    private String descripcion;

    // Acepta: fechaHoraInicio | fechaDesde | inicio (ISO o epoch ms/seg)
    @JsonAlias({"fechaDesde", "inicio"})
    @JsonDeserialize(using = IsoOrEpochLocalDateTimeDeserializer.class)
    private LocalDateTime fechaHoraInicio;

    // Acepta: fechaHoraFin | fechaHasta | fin (ISO o epoch ms/seg)
    @JsonAlias({"fechaHasta", "fin"})
    @JsonDeserialize(using = IsoOrEpochLocalDateTimeDeserializer.class)
    private LocalDateTime fechaHoraFin;

    // Acepta "direccion"
    @JsonAlias("direccion")
    private String direccionUbicacion;

    private BigDecimal longitudUbicacion;
    private BigDecimal latitudUbicacion;

    // Acepta "precio"
    @JsonAlias("precio")
    private BigDecimal precioInscripcion;

    // Acepta "maxParticipantes"
    @JsonAlias("maxParticipantes")
    private Integer cantidadMaximaInvitados;        // si no lo usás queda null

    @JsonAlias("maxParticipantes")
    private Integer cantidadMaximaParticipantes;

    private BigDecimal precioOrganizacion;

    private Long superEventoId;

    // Acepta "tipoInscripcion"
    /*@JsonAlias("tipoInscripcion")
    private Long tipoInscripcionEventoId;*/

    //private Long administradorEventoId;

    // Acepta "idEspacio"
    @JsonAlias("idEspacio")
    private Long espacioId;

    // El front manda "disciplinas": [id,...]
    private List<DTODisciplinaEventoCreate> disciplinasEvento;

    /* =======================
       Setters de compatibilidad
       ======================= */

    // Permite recibir: "ubicacion": { "latitud": -32.9, "longitud": -68.87 }
    @JsonProperty("ubicacion")
    public void setUbicacion(Geo ubicacion) {
        if (ubicacion != null) {
            if (ubicacion.getLatitud() != null) {
                this.latitudUbicacion = BigDecimal.valueOf(ubicacion.getLatitud());
            }
            if (ubicacion.getLongitud() != null) {
                this.longitudUbicacion = BigDecimal.valueOf(ubicacion.getLongitud());
            }
        }
    }

    // "disciplinas": [1,2,3] -> lista de DTODisciplinaEventoCreate con disciplina.id
    @JsonProperty("disciplinas")
    public void setDisciplinas(List<Long> ids) {
        if (ids == null) {
            this.disciplinasEvento = null;
            return;
        }
        this.disciplinasEvento = ids.stream()
                .filter(Objects::nonNull)
                .map(id -> DTODisciplinaEventoCreate.builder()
                        .disciplina(DTODisciplinaRef.builder().id(id).build())
                        .build())
                .collect(Collectors.toList());
    }

    // Si el front los envía, se ignoran sin romper
    @JsonProperty("usarCronograma")
    public void setUsarCronograma(Boolean ignore) { /* no-op */ }

    @JsonProperty("horarioId")
    public void setHorarioId(Long ignore) { /* no-op */ }

    /* Clase auxiliar para "ubicacion" */
    @Data
    public static class Geo {
        private Double latitud;
        private Double longitud;
    }
}
