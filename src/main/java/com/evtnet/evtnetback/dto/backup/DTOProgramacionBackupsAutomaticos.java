package com.evtnet.evtnetback.dto.backup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTOProgramacionBackupsAutomaticos {
    private Frecuencia frecuencia;
    private Long fechaHoraInicio;
    private Integer copiasIncrementalesPorCompleta;
    private Integer copiasAnterioresAConservar;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Frecuencia {
        private Integer meses;
        private Integer dias;
        private Integer horas;
    }
}
