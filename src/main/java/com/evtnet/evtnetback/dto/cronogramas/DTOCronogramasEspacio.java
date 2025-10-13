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
public class DTOCronogramasEspacio{
    private String nombre;
    private List<DTOCronograma> cronogramas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DTOCronograma {
        private Long id;
        private Long fechaDesde;
        private Long fechaHasta;
        private int diasHaciaAdelante;
    }
}
