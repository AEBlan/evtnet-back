package com.evtnet.evtnetback.dto.tipoCalificacion;

import com.evtnet.evtnetback.dto.motivoCalificacion.DTOMotivoCalificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class DTOTipoCalificacion {
    private Long id;
    private String nombre;
    private String url;
    private Long fechaAlta;
    private Long fechaBaja;
    private String contentType;
    private List<DTOMotivoCalificacion> motivos;
}
