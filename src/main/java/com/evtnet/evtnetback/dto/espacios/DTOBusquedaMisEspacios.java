package com.evtnet.evtnetback.dto.espacios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOBusquedaMisEspacios {
    private String texto;
    private boolean propietario;
    private boolean administrador;
    private String username;
}

