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
public class DTOActualizarCaracteristicasSubespacio {
    private Long idSubEspacio;
    private List<DTOCaracteristicas> caracteristicas;

    @Data
    public static class DTOCaracteristicas{
        private Long id;
        private Long idIconoCaracteristica;
        private Long idEspacio;
        private String nombre;
        private String urlIcono;
        private String contentType;
    }
}
