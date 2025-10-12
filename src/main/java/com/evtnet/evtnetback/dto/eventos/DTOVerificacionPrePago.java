package com.evtnet.evtnetback.dto.eventos;

import lombok.*;
import java.util.List;

import com.evtnet.evtnetback.dto.usuarios.DTOPreferenciaPago;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOVerificacionPrePago {
    private boolean valido;
    private List<DTOPreferenciaPago> preferencias; 
}

