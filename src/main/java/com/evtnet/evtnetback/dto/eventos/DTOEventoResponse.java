package com.evtnet.evtnetback.dto.eventos;

import com.evtnet.evtnetback.dto.disciplinaevento.DTODisciplinaEventoResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOEventoResponse {
    private Long id;
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

    private Long superEventoId;
    private Long tipoInscripcionEventoId;
    private Long modoEventoId;
    private Long administradorEventoId;
    private Long espacioId;

    // 👇 DisciplinaEvento ya asociadas al evento (sin recursión)
    private List<DTODisciplinaEventoResponse> disciplinasEvento;
}

