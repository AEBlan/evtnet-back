package com.evtnet.evtnetback.dto.grupos;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOGrupo {
    private Long id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaAlta;
    private Long idChat;
    private List<Participante> participantes;
    private boolean esAdministrador;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Participante {
        private String username;
        private String nombre;
        private String apellido;
        private LocalDateTime fechaHoraUnion; // primera vez que se unió
    }
}
