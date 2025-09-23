package com.evtnet.evtnetback.dto.eventos;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class DTODatosParaInscripcion {
    private String nombreEvento;
    private Integer cantidadMaximaInvitados;
    private Integer limiteParticipantes;
    private Boolean esAdministrador;
    private Boolean esOrganizador;
}
