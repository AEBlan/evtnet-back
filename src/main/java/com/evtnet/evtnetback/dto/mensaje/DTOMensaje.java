package com.evtnet.evtnetback.dto.mensaje;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DTOMensaje {
    private Long chatId;
    private String texto;
}