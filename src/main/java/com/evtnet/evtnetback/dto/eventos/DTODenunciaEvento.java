package com.evtnet.evtnetback.dto.eventos;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTODenunciaEvento {
    private Long idEvento;
    private String titulo;
    private String descripcion;
}

