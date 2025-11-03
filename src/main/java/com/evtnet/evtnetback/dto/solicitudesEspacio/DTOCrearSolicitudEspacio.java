package com.evtnet.evtnetback.dto.solicitudesEspacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOCrearSolicitudEspacio {
    private String nombre;
    private String descripcion;
    private String direccion;
    private double latitud;
    private double longitud;
    private String justificacion;
    private boolean esPublico;
    private Long sepId;
}
