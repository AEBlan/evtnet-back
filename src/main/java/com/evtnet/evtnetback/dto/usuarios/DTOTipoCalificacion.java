package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOTipoCalificacion {
    private Long id;
    private String nombre;
    private List<DTOMotivoCalificacionSimple> motivos;
}
