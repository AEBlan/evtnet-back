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
public class DTOModificarInstanciaMascota {
    private Long id;
    private String nombre;
    private String descripcion;
    private String pageSelector;
    private String selector;
    private List<Long> eventos;
    private List<SecuenciaItem> imagenes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecuenciaItem {
        private String texto;
        private Long imagenId;
    }
}