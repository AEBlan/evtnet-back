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
public class DTODetalleCronograma {
    private Long id;
    private String nombreSubEspacio;
    private Long fechaDesde;
    private Long fechaHasta;
    private List<Horario> horarios;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Horario{
        private Long id;
        private int diaSemana;
        private Long horaDesde;
        private Long horaHasta;
        private double precioOrganizacion;
        private double adicionalPorInscripcion;
    }
}
