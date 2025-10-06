package com.evtnet.evtnetback.dto.grupos;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOCrearGrupo {
    private String nombre;
    private String descripcion;
    private List<Participante> participantes;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Participante {
        private String username; // obligatorio
        private Long tipo;       // id de TipoUsuarioGrupo
    }
}
