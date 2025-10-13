// cronogramas/DTODatosCreacionExcepcion.java
package com.evtnet.evtnetback.dto.cronogramas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTODatosCreacionExcepcion{
    private String nombreSubEspacio;
    private Long fechaDesde;
    private Long fechaHasta;
    private List<TipoExcepcion> tiposExcepcion;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TipoExcepcion {
        private Long id;
        private String nombre;
    }
}

