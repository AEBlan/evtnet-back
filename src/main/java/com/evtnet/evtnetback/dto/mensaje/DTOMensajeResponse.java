package com.evtnet.evtnetback.dto.mensaje;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOMensajeResponse {
    private Long id;
    private Long chatId;
    private Long usuarioId;
    private String usuarioNombre;
    private String texto;
    private LocalDateTime fechaHora;
}
