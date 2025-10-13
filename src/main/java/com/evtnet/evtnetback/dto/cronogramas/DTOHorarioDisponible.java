package com.evtnet.evtnetback.dto.cronogramas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOHorarioDisponible {
    private Long id;
    private Long fechaHoraDesde;
    private Long fechaHoraHasta;
    private double precioOrganizacion;
    private double adicionalPorInscripcion;
}
