package com.evtnet.evtnetback.dto.espacios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOBusquedaEspacios {
    private String texto;
    private Ubicacion ubicacion;
    private List<Long> tipos;
    private List<Long> disciplinas;

    @Data
    public static class Ubicacion {
        private Double latitud;
        private Double longitud;
        private Double rango;
    }
}

