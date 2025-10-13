package com.evtnet.evtnetback.dto.imagenes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOObtenerImagenEspacio {
    private Long id;
    private String contentType;
    private String content;
}
