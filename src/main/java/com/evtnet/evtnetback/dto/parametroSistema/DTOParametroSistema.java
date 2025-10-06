package com.evtnet.evtnetback.dto.parametroSistema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTOParametroSistema {
    private Long id;
    private String nombre;
    private String valor;
}
