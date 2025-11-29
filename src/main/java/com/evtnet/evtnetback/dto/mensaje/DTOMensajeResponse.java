package com.evtnet.evtnetback.dto.mensaje;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOMensajeResponse {
    private Long id;
    private String username;
    private String usuarioNombre;
    private String usuarioApellido;
    private String texto;
    private LocalDateTime fechaHora;
}
