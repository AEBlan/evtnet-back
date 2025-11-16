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
public class DTOInstanciaMascotaPagina {
    private Long id;
    private String nombre;
    private String selector;
    private List<String> eventos;
    private List<SecuenciaItem> secuencia;
    private boolean visualizado;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecuenciaItem {
        private String texto;
        private String url;
    }
}