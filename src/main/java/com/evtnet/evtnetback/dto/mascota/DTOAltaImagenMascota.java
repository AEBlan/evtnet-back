package com.evtnet.evtnetback.dto.mascota;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DTOAltaImagenMascota {
    private String url;
    private String nombre;
}
