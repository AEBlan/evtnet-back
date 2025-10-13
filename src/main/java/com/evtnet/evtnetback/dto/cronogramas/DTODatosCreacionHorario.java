package com.evtnet.evtnetback.dto.cronogramas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTODatosCreacionHorario {
    private String nombreSubEspacio;
    private Long fechaDesde;
    private Long fechaHasta;
    private double comisionOrganizacion;
    private double comisionInscripcion;
}

