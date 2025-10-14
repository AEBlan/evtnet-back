package com.evtnet.evtnetback.dto.espacios;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOEncargadoSubespacio {
    private Long idSubespacio;
    private String nombreSubespacio;
    private String nombreApellidoEncargado;
    private String username;
    private String urlFotoPerfil;
    private String contentType;
}
