package com.evtnet.evtnetback.dto.eventos;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOCambioEstadoDenuncia {
    private Long idDenuncia;
    private Long estado;
    private String descripcion;
}
