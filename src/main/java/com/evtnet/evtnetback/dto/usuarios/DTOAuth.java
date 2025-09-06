package com.evtnet.evtnetback.dto.usuarios;

import lombok.*;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor 
public class DTOAuth {
    private String token;
    private List<String> permisos;
    private String username;
}