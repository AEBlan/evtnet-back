package com.evtnet.evtnetback.dto.mascota;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOAltaInstanciaMascota {
    private String nombre;
    private String descripcion;
    private String pageRegex;
    private String selector;
    private String events;
    private List<SecuenciaItem> imagenes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecuenciaItem {
        private String texto;
        private Long imagenId;
    }
}