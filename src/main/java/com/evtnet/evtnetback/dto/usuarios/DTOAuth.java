package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DTOAuth {
    private String token;
    private List<String> permisos;
    private String username;
}