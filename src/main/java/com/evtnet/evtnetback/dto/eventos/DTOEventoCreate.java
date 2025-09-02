// eventos/DTOCrearEvento.java
package com.evtnet.evtnetback.dto.eventos;

import com.evtnet.evtnetback.dto.disciplinaevento.DTODisciplinaEventoCreate;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOEventoCreate {
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private String direccionUbicacion;
    private BigDecimal longitudUbicacion;
    private BigDecimal latitudUbicacion;
    private BigDecimal precioInscripcion;
    private Integer cantidadMaximaInvitados;
    private Integer cantidadMaximaParticipantes;
    private BigDecimal precioOrganizacion;

    private Long superEventoId;            // opcional si lo usás
    private Long tipoInscripcionEventoId;  // opcional
    private Long modoEventoId;             // opcional
    private Long administradorEventoId;    // opcional
    private Long espacioId;                // opcional

    // 👇 Lista de DisciplinaEvento a crear para este Evento
    private List<DTODisciplinaEventoCreate> disciplinasEvento;
}


