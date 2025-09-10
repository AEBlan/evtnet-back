// dto/iconos/DTOIconoCaracteristica.java
package com.evtnet.evtnetback.dto.iconos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DTOIconoCaracteristica {
    private Long id;
    private String imagenUrl;
    private String fechaHoraAlta; // ISO string
}
