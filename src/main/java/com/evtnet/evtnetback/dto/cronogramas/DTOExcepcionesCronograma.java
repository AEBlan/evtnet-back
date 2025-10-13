// cronogramas/DTOExcepcionesCronograma.java
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
public class DTOExcepcionesCronograma{
    private Long id;
    private String nombreSubEspacio;
    private Long fechaDesde;
    private Long fechaHasta;
    private List<Excepcion> excepciones;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Excepcion{
        private Long id;
        private Long fechaHoraDesde;
        private Long fechaHoraHasta;
        private String tipo;
        private boolean hayEventosProgramados;
    }
}
