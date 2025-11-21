package com.evtnet.evtnetback.dto.grupos;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTOGrupoMisGrupos {
    private Long id;
    private String nombre;
    private Long idChat;
    private Boolean aceptado;
}