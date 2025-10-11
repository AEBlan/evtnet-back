package com.evtnet.evtnetback.dto.eventos;

import lombok.*;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTODatosParaCambioEstadoDenuncia {
    private String titulo;
    private List<EstadoDTO> estados;

    @Builder @Data
    public static class EstadoDTO {
        private Long id;
        private String nombre;
    }
}
