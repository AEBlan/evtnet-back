// dto/imagenes/DTOImagenEspacio.java
package com.evtnet.evtnetback.dto.imagenes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOImagenEspacio {
    private Long id;
    private Long espacioId;
    private String imagen;
    private Integer orden;
    private String fechaHoraAlta; // ISO string
}
