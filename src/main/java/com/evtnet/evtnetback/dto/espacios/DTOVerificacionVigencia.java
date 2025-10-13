package com.evtnet.evtnetback.dto.espacios;

import com.evtnet.evtnetback.dto.cronogramas.DTOCronogramasEspacio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOVerificacionVigencia {
    private List<DTOCronogramasEspacio.DTOCronograma>cronogramasSuperpuestos;
    private List<DTOEvento>eventosProblematicos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DTOEvento{
        private Long id;
        private String nombre;
        private Long fechaHoraInicio;
    }
}

