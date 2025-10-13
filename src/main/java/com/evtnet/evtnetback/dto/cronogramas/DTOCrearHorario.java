package com.evtnet.evtnetback.dto.cronogramas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOCrearHorario {
    private Long idCronograma;
    private int diaSemana;
    private Long horaDesde;
    private Long horaHasta;
    private double precioOrganizacion;
    private double adicionalPorInscripcion;
}
