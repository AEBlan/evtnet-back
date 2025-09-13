package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOCalificacionRequest {
    private Long calificacionTipo;     // id del tipo
    private String usuarioCalificado;  // username destino
    private List<Long> motivos;        // ids de motivos
    private String descripcion;        // texto (para denuncias)
}
