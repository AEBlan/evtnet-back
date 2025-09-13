package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOLoginGoogle {
    private String idToken;
}