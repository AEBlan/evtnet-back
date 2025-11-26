package com.evtnet.evtnetback.dto.chat;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOChatResponse {
    private Long id;
    private String tipo;
    private LocalDateTime fechaHoraAlta;
    private Long espacioId;
    private String nombreEspacio;
    private Long eventoId;
    private String nombreEvento;
    private Long superEventoId;
    private String nombreSuperEvento;
}
