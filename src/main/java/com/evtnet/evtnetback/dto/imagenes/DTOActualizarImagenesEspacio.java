package com.evtnet.evtnetback.dto.imagenes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOActualizarImagenesEspacio {
    private Long idEspacio;
    private List<Imagen> imagenes;

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Imagen{
        private Long id;
        private int orden;
        private String blobUrl;
    }
}
