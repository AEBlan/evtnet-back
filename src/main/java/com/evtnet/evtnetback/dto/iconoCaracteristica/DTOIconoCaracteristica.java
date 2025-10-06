package com.evtnet.evtnetback.dto.iconoCaracteristica;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DTOIconoCaracteristica {
    private Long id;
    private String url;
    private Long fechaAlta;
    private Long fechaBaja;
    private String contentType;
}
