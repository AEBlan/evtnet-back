package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOLoginGoogle {
    private String idToken;
}