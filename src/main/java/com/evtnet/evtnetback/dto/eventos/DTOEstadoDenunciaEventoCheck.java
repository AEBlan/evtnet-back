package com.evtnet.evtnetback.dto.eventos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DTOEstadoDenunciaEventoCheck {
    private Long id;
    private String nombre;
    private boolean checked;
}

