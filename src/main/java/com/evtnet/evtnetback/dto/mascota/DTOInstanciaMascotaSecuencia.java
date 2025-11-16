package com.evtnet.evtnetback.dto.mascota;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DTOInstanciaMascotaSecuencia {
    private String texto;
    private Long imagenId;
    private Integer orden;
}