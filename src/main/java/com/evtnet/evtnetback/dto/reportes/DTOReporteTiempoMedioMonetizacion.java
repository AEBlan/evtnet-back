package com.evtnet.evtnetback.dto.reportes;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DTOReporteTiempoMedioMonetizacion {

    private LocalDateTime fechaHoraGeneracion;
    private List<Item> datos;

    @Data
    public static class Item {
        private LocalDateTime inicio;
        private LocalDateTime fin;
        private List<Medio> medios;
    }

    @Data
    public static class Medio {
        private String nombre;
        private Double monto;
    }
}
