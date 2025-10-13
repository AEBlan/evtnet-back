package com.evtnet.evtnetback.dto.eventos;

import com.evtnet.evtnetback.dto.disciplinaevento.DTODisciplinaEventoCreate;
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

    // ==========================
    // 🔹 Datos generales
    // ==========================
    private String nombre;
    private String descripcion;

    @JsonDeserialize(using = IsoOrEpochLocalDateTimeDeserializer.class)
    private LocalDateTime fechaHoraInicio;

    @JsonDeserialize(using = IsoOrEpochLocalDateTimeDeserializer.class)
    private LocalDateTime fechaHoraFin;

    private BigDecimal precioInscripcion;
    private Integer cantidadMaximaInvitados;
    private Integer cantidadMaximaParticipantes;

    /**
     * Monto opcional adicional que se cobra al organizador.
     * Solo aplicable para espacios privados.
     */
    private BigDecimal precioOrganizacion;

    // ==========================
    // 🔹 Estructura y relaciones
    // ==========================
    /**
     * Id del subespacio en el cual se realizará el evento.
     * Campo requerido.
     */
    @JsonAlias({"idSubespacio", "subEspacioId", "idEspacio"})
    private Long subEspacioId;

    /**
     * Id del super evento al cual pertenece, si aplica.
     */
    private Long superEventoId;

    /**
     * Lista de disciplinas seleccionadas para el evento.
     * Solo se permiten aquellas soportadas por el subespacio.
     */
    private List<DTODisciplinaEventoCreate> disciplinasEvento;

    // ==========================
    // 🔹 Cronograma / permisos
    // ==========================

    /**
     * Si el evento fue creado a partir de un cronograma,
     * se envía el ID de la configuración elegida.
     * Solo obligatorio para usuarios que no son administradores del espacio.
     */
    private Long cronogramaId;

    /**
     * Indica si el usuario está creando un evento fuera de cronograma
     * (solo permitido para administradores o propietarios de espacios privados).
     */
    @Builder.Default
    private boolean horarioLibre = false;

    /**
     * Campo opcional — útil para el front,
     * permite validar antes de crear si el horario libre es permitido.
     */
    @JsonProperty("puedeElegirHorarioLibre")
    private Boolean puedeElegirHorarioLibre;

    /**
     * Campo informativo — podría usarse si el front necesita saber
     * si el espacio donde se crea el evento requiere aprobación manual.
     */
    private Boolean requiereAprobacion;
}
