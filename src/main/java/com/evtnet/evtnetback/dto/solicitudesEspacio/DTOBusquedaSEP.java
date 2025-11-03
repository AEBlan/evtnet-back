package com.evtnet.evtnetback.dto.solicitudesEspacio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOBusquedaSEP {
    private String texto;
    private Ubicacion ubicacion;
    private List<Long> tipos;
    private List<Long> espacios;
    private List<Long> estados;
    private Long fechaIngresoDesde;
    private Long fechaIngresoHasta;
    private Long fechaUltimoCambioEstadoDesde;
    private Long fechaUltimoCambioEstadoHasta;

    @Data
    public static class Ubicacion {
        private Double latitud;
        private Double longitud;
        private Double rango;
    }
}
