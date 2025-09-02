// dto/imagenes/DTOImagenEspacio.java
package com.evtnet.evtnetback.dto.imagenes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DTOImagenEspacio {
    private Long id;
    private Long espacioId;
    private String imagenUrl;
    private Integer orden;
    private String fechaHoraAlta; // ISO string
}
